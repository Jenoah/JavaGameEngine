package game.demo;

import nl.jenoah.core.EngineManager;
import nl.jenoah.core.ModelManager;
import nl.jenoah.core.MouseInput;
import nl.jenoah.core.entity.*;
import nl.jenoah.core.fonts.fontMeshCreator.FontType;
import nl.jenoah.core.fonts.fontMeshCreator.GUIText;
import nl.jenoah.core.gui.GuiObject;
import nl.jenoah.core.lighting.DirectionalLight;
import nl.jenoah.core.lighting.PointLight;
import nl.jenoah.core.lighting.SpotLight;
import nl.jenoah.core.shaders.ShaderManager;
import nl.jenoah.core.utils.Calculus;
import nl.jenoah.core.utils.Conversion;
import nl.jenoah.core.utils.Transformation;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class Level0 extends Scene {

    private Entity proxyEntity;

    //Labels
    private GUIText fpsLabel;
    private GUIText positionLabel;
    private GUIText resolutionLabel;

    @Override public void init() {
        super.init();
        levelName = "Level 0";
        setFogColor(new Vector3f(0.6f, 0.65f, .8f));
        setFogDensity(.01f);

        //Models
        Model barnModel = modelManager.getObjLoader().loadOBJModel("/models/barn.obj");
        barnModel.setTexture(new Texture(modelManager.getTextureLoader().loadTexture("textures/barn.png")), 32f);
        Entity barn = new Entity(barnModel, new Vector3f(0, -1f, -10f), new Vector3f(0, 0, 0), 1f);
        addEntity(barn);

        Model monkModel = modelManager.getObjLoader().loadOBJModel("/models/monk.obj");
        monkModel.setTexture(new Texture(modelManager.getTextureLoader().loadTexture("textures/blockPallete.png")), 32f);
        Entity monk = new Entity(monkModel, new Vector3f(0, 0.5f, -10f), new Vector3f(0, 0, 0), 1);
        addEntity(monk);

        Model pointLightProxyModel = modelManager.getObjLoader().loadOBJModel("/models/cube.obj");
        pointLightProxyModel.setTexture(new Texture(modelManager.getTextureLoader().loadTexture("textures/blockPallete.png")), .8f);
        proxyEntity = new Entity(pointLightProxyModel, new Vector3f(0, 0.5f, -7f), new Vector3f(0, 0, 0), .1f);
        addEntity(proxyEntity);

        Texture lightProxyTexture = new Texture(modelManager.getTextureLoader().loadTexture("textures/barn.png", false, false));
        Billboard lightProxy = new Billboard(modelManager.getPrimitiveLoader().getQuad(), lightProxyTexture, 0.1f);
        lightProxy.setPosition(new Vector3f(0, 1f ,0));
        addEntity(lightProxy);
        lightProxy.setParent(proxyEntity);

        Model groundModel = modelManager.getPrimitiveLoader().getQuad();
        groundModel.setTexture(new Texture(modelManager.getTextureLoader().loadTexture("sprites/square.png")), 1f);
        groundModel.getMaterial().setAmbientColor(new Vector4f(0.3f, 0.75f, 0.15f, 1));
        Entity groundEntity = new Entity(groundModel, new Vector3f(0, -1, 0), new Vector3f(-90, 0, 0), 20);
        addEntity(groundEntity);


        //Lighting
        //Directional Light
        Vector3f lightColor = new Vector3f(1f, 1f, .75f);
        Vector3f lightDirection = new Vector3f(-1f, 1f, 2f);
        lightDirection.normalize();

        DirectionalLight directionalLight = new DirectionalLight(lightColor, lightDirection, 1);
        directionalLight.showProxy();
        setDirectionalLight(directionalLight);

        //Point light
        Vector3f pointLightPosition = new Vector3f(0);
        Vector3f pointLightColor = new Vector3f(1f, 0f, 1f);
        PointLight pointLight = new PointLight(pointLightColor, pointLightPosition, .8f, 5f);

        pointLight.setParent(proxyEntity);

        addPointLight(pointLight);

        Vector3f spotLightConeDirection = new Vector3f(0f, 0.0f, -1f);
        float spotLightCutoff = org.joml.Math.cos(Math.toRadians(30));
        float spotLightOuterCutOff = org.joml.Math.cos(Math.toRadians(35));
        SpotLight spotLight1 = new SpotLight(new Vector3f(0.4f, 0, 0), new Vector3f(0f), 3f, 1.0f, 0.09f, 0.032f, spotLightConeDirection, spotLightCutoff, spotLightOuterCutOff);
        SpotLight spotLight2 = new SpotLight(new Vector3f(0, 0.4f, 0), new Vector3f(0f), 3f, 1.0f, 0.09f, 0.032f, Calculus.subtractVectors(new Vector3f(0, 0, 0), spotLightConeDirection), spotLightCutoff, spotLightOuterCutOff);

        spotLight1.setParent(proxyEntity);
        spotLight2.setParent(proxyEntity);

        addSpotLight(spotLight1);
        addSpotLight(spotLight2);



        ShaderManager.getInstance().getLitShader().setLights(getDirectionalLight(), getPointLights(), getSpotLights());

        addGameObject(player);

        //UI
        GuiObject testSprite = new GuiObject(ModelManager.getInstance().getTextureLoader().loadTexture("sprites/square.png", true), new Vector2f(-.7f, .7f), new Vector2f(.25f));
        testSprite.setColor(new Vector4f(0.1f, 0.1f, 0.1f, 0.7f));
        addGUI(testSprite);

        //Text
        FontType jetbrainFontType = new FontType(ModelManager.getInstance().getTextureLoader().loadTexture("fonts/jetbrains/jetbrains.png", false, false), new File("fonts/jetbrains/jetbrains.fnt"));
        GUIText statusLabel = new GUIText("Stats", 1.25f, jetbrainFontType, new Vector2f(0.03f, 0.03f), 0.25f, false);
        addText(statusLabel);

        fpsLabel = new GUIText("FPS: xxx", 1f, jetbrainFontType, new Vector2f(0.03f, 0.075f), 0.25f, false);
        addText(fpsLabel);

        positionLabel = new GUIText("Position: x", 1f, jetbrainFontType, new Vector2f(0.03f, 0.1f), 0.25f, false);
        addText(positionLabel);

        resolutionLabel = new GUIText("Resolution: x", 1f, jetbrainFontType, new Vector2f(0.03f, 0.125f), 0.25f, false);
        addText(resolutionLabel);
    }

    @Override
    public void handleInput() {
        super.handleInput();

        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_UP)){
            proxyEntity.setPosition(Calculus.addVectors(proxyEntity.getPosition(), new Vector3f(0, 0, -0.05f)));
        }
        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_DOWN)){
            proxyEntity.setPosition(Calculus.addVectors(proxyEntity.getPosition(), new Vector3f(0, 0, 0.05f)));
        }
    }

    @Override
    public void update(MouseInput mouseInput) {
        super.update(mouseInput);
        getEntities().get(1).lookAt(player.getPosition());

        getSpotLights()[0].setConeDirection(Transformation.rotateDirection(getSpotLights()[0].getConeDirection(), new Vector3f(0, 1.5f, 0)));
        getSpotLights()[1].setConeDirection(Transformation.rotateDirection(getSpotLights()[1].getConeDirection(), new Vector3f(0, 1.5f, 0)));

        if(EngineManager.getFrameCount() % 30 != 0) return;
        fpsLabel.setText("FPS: " + EngineManager.getFps());
        positionLabel.setText("Position: " + Conversion.V3ToString(player.getPosition()));
        resolutionLabel.setText("Resolution: " + windowManager.getWidth() + " x " + windowManager.getHeight());
    }
}