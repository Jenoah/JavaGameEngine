package game.demo;

import game.terrain.MarchingChunk;
import game.terrain.TerrainGeneration;
import game.utils.ChunkCoord;
import nl.jenoah.core.EngineManager;
import nl.jenoah.core.MouseInput;
import nl.jenoah.core.components.RenderComponent;
import nl.jenoah.core.entity.*;
import nl.jenoah.core.fonts.fontMeshCreator.FontType;
import nl.jenoah.core.fonts.fontMeshCreator.GUIText;
import nl.jenoah.core.gui.GuiObject;
import nl.jenoah.core.lighting.DirectionalLight;
import nl.jenoah.core.lighting.PointLight;
import nl.jenoah.core.lighting.SpotLight;
import nl.jenoah.core.loaders.OBJLoader.OBJLoader;
import nl.jenoah.core.loaders.PrimitiveLoader;
import nl.jenoah.core.loaders.TextureLoader;
import nl.jenoah.core.rendering.MeshMaterialSet;
import nl.jenoah.core.shaders.ShaderManager;
import nl.jenoah.core.utils.Calculus;
import nl.jenoah.core.utils.Conversion;
import nl.jenoah.core.utils.Transformation;
import nl.jenoah.core.utils.Utils;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.List;
import java.util.Queue;

public class Level0 extends Scene {

    private GameObject proxyEntity;
    private GameObject monkeyEntity;

    //Labels
    private GUIText fpsLabel;
    private GUIText positionLabel;
    private GUIText resolutionLabel;

    private float UIUpdateTime = 0f;

    //Terrain
    private final int renderDistance = 4;
    private TerrainGeneration terrainGeneration;

    @Override public void init() {
        super.init();
        levelName = "Level 0";

        terrainGeneration = new TerrainGeneration(renderDistance);
        terrainGeneration.setSurfaceFeatureDensity(0.7f);
        terrainGeneration.setSurfaceFeatureSamples(8);

        Utils.setNoiseSeed(123);
        player.setPosition(0, 6.5f, 0);

        setFogColor(new Vector3f(0.7f, 0.75f, .8f));
        setFogDensity(.025f);
        setFogGradient(10f);

        //Textures
        Texture blockPaletteTexture = new Texture(TextureLoader.loadTexture("textures/blockPallete.png"));

        //Models
        List<MeshMaterialSet> barnMeshMaterialSets = OBJLoader.loadOBJModel("/models/barn.obj");
        GameObject barn = new GameObject().setPosition(new Vector3f(0, 5f, -10f));
        barn.addComponent(new RenderComponent(barnMeshMaterialSets));
        addEntity(barn);

        List<MeshMaterialSet> monkMeshMaterialSets = OBJLoader.loadOBJModel("/models/monk.obj", blockPaletteTexture);
        monkeyEntity = new GameObject().setPosition(new Vector3f(0, 6.5f, -10f));
        monkeyEntity.addComponent(new RenderComponent(monkMeshMaterialSets));
        addEntity(monkeyEntity);


        MeshMaterialSet groundBlock = new MeshMaterialSet(PrimitiveLoader.getCube().getMesh().calculateNormals());
        groundBlock.material.setAlbedoTexture(new Texture("textures/rock/rock_albedo.jpg"));
        groundBlock.material.setNormalMap(new Texture("textures/rock/rock_normal.jpg", false, false, true, true));
        groundBlock.material.setReflectance(64);
        GameObject groundBlockEntity = new GameObject().setPosition(new Vector3f(0, 2.5f, -10)).setScale(new Vector3f(10, 5, 15));
        groundBlockEntity.addComponent(new RenderComponent(groundBlock.mesh, groundBlock.material));
        addEntity(groundBlockEntity);

        GameObject lightProxy = new GameObject().setPosition(new Vector3f(0, 6f, -7f)).setScale(0.1f);
        Material billboardMaterial = new Material(ShaderManager.billboardShader);
        billboardMaterial.setAlbedoTexture(new Texture("textures/Prozac.jpeg", false, false));
        proxyEntity = lightProxy;
        lightProxy.addComponent(new RenderComponent(PrimitiveLoader.getQuad().getMesh(), billboardMaterial));
        addEntity(lightProxy);

        List<MeshMaterialSet> treeMeshMaterialSet = OBJLoader.loadOBJModel("/models/birch.obj");
        treeMeshMaterialSet.forEach((meshMaterialSet -> meshMaterialSet.mesh.generateUVs()));
        GameObject tree = new GameObject().setPosition(5, 5f, -2);
        tree.addComponent(new RenderComponent(treeMeshMaterialSet));
        addEntity(tree);

        terrainGeneration.setSurfaceFeature(tree);

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
        PointLight pointLight = new PointLight(pointLightColor, pointLightPosition, 5f, 1f);

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

        addGameObject(player);

        //UI
        GuiObject testSprite = new GuiObject(new Vector2f(-.7125f, .675f), new Vector2f(.25f, .275f));
        testSprite.setColor(new Vector4f(0.1f, 0.1f, 0.1f, 0.7f));
        addGUI(testSprite);

        //Text
        FontType jetbrainFontType = new FontType(TextureLoader.loadTexture("fonts/jetbrains/jetbrains.png", false, false), new File("fonts/jetbrains/jetbrains.fnt"));
        GUIText statusLabel = new GUIText("Stats", 1.25f, jetbrainFontType, new Vector2f(0.03f, 0.03f), 0.25f, false);
        addText(statusLabel);

        fpsLabel = new GUIText("FPS: xxx", 1f, jetbrainFontType, new Vector2f(0.03f, 0.075f), 0.25f, false);
        addText(fpsLabel);

        positionLabel = new GUIText("Position: x", 1f, jetbrainFontType, new Vector2f(0.03f, 0.1f), 0.25f, false);
        addText(positionLabel);

        resolutionLabel = new GUIText("Resolution: x", 1f, jetbrainFontType, new Vector2f(0.03f, 0.125f), 0.25f, false);
        addText(resolutionLabel);

        GUIText instructionLabel = new GUIText(" Move: WASD + Q and E \nRotate: RMB + move mouse \nHigher speed: Left shift \nMove cat and lights: Arrows", 1f, jetbrainFontType, new Vector2f(0.03f, 0.16f), 0.25f, false);
        addText(instructionLabel);

        //Shaders
        int grassTextureID = TextureLoader.loadTexture("textures/grass.jpg");
        int rockTextureID = TextureLoader.loadTexture("textures/rock/rock_albedo.jpg");
        ShaderManager.triplanarShader.setTextureIDs(grassTextureID, rockTextureID);
        ShaderManager.triplanarShader.setBlendFactor(32);

        ShaderManager.litShader.setLights(getDirectionalLight(), getPointLights(), getSpotLights());
        ShaderManager.triplanarShader.setLights(getDirectionalLight(), getPointLights(), getSpotLights());
        ShaderManager.pbrShader.setLights(getDirectionalLight(), getPointLights(), getSpotLights());
    }

    @Override
    public void postStart() {
        super.postStart();
        terrainGeneration.start();
        terrainGeneration.setUpdatePosition(player.getPosition());
    }

    @Override
    public void handleInput() {
        super.handleInput();

        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_UP)){
            proxyEntity.setPosition(Calculus.addVectors(proxyEntity.getPosition(), new Vector3f(0, 0, -5f * EngineManager.getDeltaTime())));
        }
        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_DOWN)){
            proxyEntity.setPosition(Calculus.addVectors(proxyEntity.getPosition(), new Vector3f(0, 0, 5f * EngineManager.getDeltaTime())));
        }
        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_LEFT)){
            proxyEntity.setPosition(Calculus.addVectors(proxyEntity.getPosition(), new Vector3f(-5f * EngineManager.getDeltaTime(), 0, 0)));
        }
        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_RIGHT)){
            proxyEntity.setPosition(Calculus.addVectors(proxyEntity.getPosition(), new Vector3f(5f * EngineManager.getDeltaTime(), 0, 0)));
        }
    }

    @Override
    public void update(MouseInput mouseInput) {
        super.update(mouseInput);
        UIUpdateTime += EngineManager.getDeltaTime();
        monkeyEntity.lookAt(player.getPosition());

        getSpotLights()[0].setConeDirection(Transformation.rotateDirection(getSpotLights()[0].getConeDirection(), new Vector3f(0, 90f * EngineManager.getDeltaTime(), 0)));
        getSpotLights()[1].setConeDirection(Transformation.rotateDirection(getSpotLights()[1].getConeDirection(), new Vector3f(0, 90f * EngineManager.getDeltaTime(), 0)));

        if(UIUpdateTime < 0.2f) return;
        UIUpdateTime = 0;
        fpsLabel.setText("FPS: " + EngineManager.getFps());
        positionLabel.setText("Position: " + Conversion.V3ToString(player.getPosition()));
        resolutionLabel.setText("Resolution: " + windowManager.getWidth() + " x " + windowManager.getHeight());
        updateTerrain();
    }

    private void updateTerrain(){
        terrainGeneration.setUpdatePosition(player.getPosition());
        Queue<MarchingChunk> marchingQueue = terrainGeneration.getMarchingChunksQueue();
        while(!marchingQueue.isEmpty()){
            MarchingChunk chunk = marchingQueue.poll();
            if(!chunk.isReady){
                terrainGeneration.requeueChunk(chunk);
                continue;
            }

            chunk.publishChunk();
            if(!ChunkCoord.compareToVector(chunk.chunkPosition, new Vector3f(0, 0, -10)) && !ChunkCoord.compareToVector(chunk.chunkPosition, new Vector3f(-1, 0, -10))) terrainGeneration.addSurfaceFeatures(chunk);

            GameObject chunkEntity = chunk.getChunkEntity();
            if(chunkEntity != null) {
                chunkEntity.getComponent(RenderComponent.class).getMeshMaterialSets().getFirst().material.setShader(ShaderManager.triplanarShader);

                addEntity(chunk.getChunkEntity());
            }
        }
        terrainGeneration.restockQueue();
    }

    @Override
    public void cleanUp() {
        terrainGeneration.end();
        super.cleanUp();
    }
}
