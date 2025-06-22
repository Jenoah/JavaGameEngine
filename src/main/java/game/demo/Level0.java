package game.demo;

import game.components.MoveWithArrows;
import game.components.RotateSpotlight;
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
import nl.jenoah.core.rendering.RenderManager;
import nl.jenoah.core.shaders.ShaderManager;
import nl.jenoah.core.utils.*;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Level0 extends Scene {

    private GameObject monkeyEntity;

    private RenderManager renderManager;

    //Labels
    private GUIText fpsLabel;
    private GUIText positionLabel;
    private GUIText resolutionLabel;
    private GUIText performanceLabel;
    private GUIText chunkLabel;

    private float UIUpdateTime = 0f;

    //Terrain
    private final int renderDistance = 4;
    private final int maxChunksPerFrame = 10;

    private TerrainGeneration terrainGeneration;
    private Thread terrainGenerationThread;
    Set<MarchingChunk> marchingQueue = new HashSet<>();

    @Override public void init() {
        super.init();
        levelName = "Level 0";
        renderManager = DemoLauncher.getGame().getRenderer();

        terrainGeneration = new TerrainGeneration(renderDistance);
        terrainGenerationThread = new Thread(terrainGeneration);
        terrainGenerationThread.setDaemon(true);
        terrainGeneration.setSurfaceFeatureDensity(0.7f);
        terrainGeneration.setSurfaceFeatureSamples(8);

        Utils.setNoiseSeed(123);
        player.setPosition(0, 6.5f, 0);
        renderManager.setRenderCamera(player.getCamera());

        setFogColor(new Vector3f(0.7f, 0.75f, .8f));
        setFogDensity(.025f);
        setFogGradient(10f);

        //Textures
        Texture blockPaletteTexture = new Texture(TextureLoader.loadTexture("textures/blockPallete.png"));

        //Models
        Set<MeshMaterialSet> barnMeshMaterialSets = OBJLoader.loadOBJModel("/models/barn.obj");
        GameObject barn = new GameObject().setPosition(new Vector3f(0, 5f, -10f));
        barn.addComponent(new RenderComponent(barnMeshMaterialSets));
        addEntity(barn);

        Set<MeshMaterialSet> monkMeshMaterialSets = OBJLoader.loadOBJModel("/models/monk.obj", blockPaletteTexture);
        monkeyEntity = new GameObject().setPosition(new Vector3f(0, 6.5f, -10f));
        monkeyEntity.addComponent(new RenderComponent(monkMeshMaterialSets));
        addEntity(monkeyEntity);

        Set<MeshMaterialSet> testMeshMaterialSets = OBJLoader.loadOBJModel("/models/monk.obj", blockPaletteTexture);
        testMeshMaterialSets.forEach((meshMaterialSet -> meshMaterialSet.material.setShader(ShaderManager.unlitShader).setAmbientColor(new Vector4f(1, 0, 1, 1))));
        GameObject testEntity = new GameObject().setPosition(new Vector3f(2, 6.5f, -3f)).setScale(0.3f).lookAt(player.getPosition());
        testEntity.addComponent(new RenderComponent(testMeshMaterialSets));
        addEntity(testEntity);

        MeshMaterialSet groundBlock = new MeshMaterialSet(PrimitiveLoader.getCube().getMesh().calculateNormals());
        groundBlock.material.setAlbedoTexture(new Texture("textures/rock/rock_albedo.jpg"));
        groundBlock.material.setNormalMap(new Texture("textures/rock/rock_normal.jpg", false, false, true, true));
        groundBlock.material.setRoughnessMap(new Texture("textures/rock/rock_roughness.jpg", false, false, true, true));
        groundBlock.material.setRoughness(.6f);
        GameObject groundBlockEntity = new GameObject().setPosition(new Vector3f(0, 2.5f, -10)).setScale(new Vector3f(10, 5, 15));
        groundBlockEntity.addComponent(new RenderComponent(groundBlock.mesh, groundBlock.material));
        addEntity(groundBlockEntity);

        GameObject lightProxy = new GameObject().setPosition(new Vector3f(0, 6f, -7f)).setScale(0.5f);
        Material billboardMaterial = new Material(ShaderManager.billboardShader);
        billboardMaterial.setAlbedoTexture(new Texture("textures/Prozac.jpeg", false, false));
        lightProxy.addComponent(new RenderComponent(PrimitiveLoader.getQuad().getMesh(), billboardMaterial));
        lightProxy.addComponent(new MoveWithArrows());
        addEntity(lightProxy);

        Set<MeshMaterialSet> treeMeshMaterialSet = OBJLoader.loadOBJModel("/models/birch.obj");
        treeMeshMaterialSet.forEach((meshMaterialSet -> meshMaterialSet.mesh.generateUVs()));
        GameObject tree = new GameObject().setPosition(5, 5f, -2);
        tree.setStatic(true);
        tree.addComponent(new RenderComponent(treeMeshMaterialSet));

        terrainGeneration.setSurfaceFeature(tree);

        //Lighting
        //Directional Light
        Vector3f lightColor = new Vector3f(1f, 1f, .75f);
        Vector3f lightDirection = new Vector3f(0.5f, -0.75f, -0.5f);
        lightDirection.normalize();

        DirectionalLight directionalLight = new DirectionalLight(lightColor, lightDirection, 1);
        directionalLight.showProxy();
        directionalLight.setPosition(-2, 7, -4);
        setDirectionalLight(directionalLight);

        //Point light
        Vector3f pointLightPosition = new Vector3f(0);
        Vector3f pointLightColor = new Vector3f(1f, 0f, 1f);
        PointLight pointLight = new PointLight(pointLightColor, pointLightPosition, 5f, 1f);

        pointLight.setParent(lightProxy);

        addPointLight(pointLight);

        Vector3f spotLightConeDirection = new Vector3f(0f, 0.0f, -1f);
        float spotLightCutoff = org.joml.Math.cos(Math.toRadians(30));
        float spotLightOuterCutOff = org.joml.Math.cos(Math.toRadians(35));
        SpotLight spotLight1 = new SpotLight(new Vector3f(0.4f, 0, 0), new Vector3f(0f), 3f, 1.0f, 0.09f, 0.032f, spotLightConeDirection, spotLightCutoff, spotLightOuterCutOff);
        SpotLight spotLight2 = new SpotLight(new Vector3f(0, 0.4f, 0), new Vector3f(0f), 3f, 1.0f, 0.09f, 0.032f, Calculus.subtractVectors(new Vector3f(0, 0, 0), spotLightConeDirection), spotLightCutoff, spotLightOuterCutOff);

        spotLight1.addComponent(new RotateSpotlight(spotLight1, new Vector3f(0, 90f, 0)));
        spotLight2.addComponent(new RotateSpotlight(spotLight2, new Vector3f(0, 90f, 0)));
        addEntity(spotLight1);
        addEntity(spotLight2);

        spotLight1.setParent(lightProxy);
        spotLight2.setParent(lightProxy);

        addSpotLight(spotLight1);
        addSpotLight(spotLight2);

        addGameObject(player);

        //UI
        GuiObject testSprite = new GuiObject(new Vector2f(-.7125f, .65f), new Vector2f(.25f, .3f));
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

        performanceLabel = new GUIText("Performance: Not measured", 1f, jetbrainFontType, new Vector2f(0.03f, 0.16f), 0.25f, false);
        addText(performanceLabel);

        chunkLabel = new GUIText("Current loaded chunks: x", 1f, jetbrainFontType, new Vector2f(0.03f, 0.28f), 0.25f, false);
        addText(chunkLabel);

        //GUIText instructionLabel = new GUIText(" Move: WASD + Q and E \nRotate: RMB + move mouse \nHigher speed: Left shift \nMove cat and lights: Arrows", 1f, jetbrainFontType, new Vector2f(0.03f, 0.16f), 0.25f, false);
        //addText(instructionLabel);

        //Shaders
        int grassTextureID = TextureLoader.loadTexture("textures/grass.jpg");
        int rockTextureID = TextureLoader.loadTexture("textures/rock/rock_albedo.jpg");
        ShaderManager.triplanarShader.setTextureIDs(grassTextureID, rockTextureID);
        ShaderManager.triplanarShader.setBlendFactor(32);

        ShaderManager.litShader.setLights(getDirectionalLight(), getPointLights(), getSpotLights());
        ShaderManager.triplanarShader.setLights(getDirectionalLight(), getPointLights(), getSpotLights());
        ShaderManager.pbrShader.setLights(getDirectionalLight(), getPointLights(), getSpotLights());

        renderManager.recordMetrics(true);
    }

    @Override
    public void postStart() {
        super.postStart();
        terrainGenerationThread.start();
        terrainGeneration.setUpdatePosition(player.getPosition());
        renderManager.shadowRenderer.setMainCamera(player.getCamera());
    }

    @Override
    public void update(MouseInput mouseInput) {
        super.update(mouseInput);
        UIUpdateTime += EngineManager.getDeltaTime();
        monkeyEntity.lookAt(player.getPosition());

        if(UIUpdateTime < 0.2f) return;
        UIUpdateTime = 0;
        fpsLabel.setText("FPS: " + EngineManager.getFps());
        positionLabel.setText("Position: " + Conversion.V3ToString(player.getPosition()));
        resolutionLabel.setText("Resolution: " + windowManager.getWidth() + " x " + windowManager.getHeight());
        performanceLabel.setText("Performance: " + renderManager.getMetrics() + " |\nFrame time: " + EngineManager.getFrameTimeMS() + "ms");
        chunkLabel.setText("Active / total chunks: " + terrainGeneration.getActiveChunkCount() + " / " + terrainGeneration.getTotalChunkCount());
        updateTerrain();
    }

    private void updateTerrain() {
        terrainGeneration.setUpdatePosition(player.getPosition());
        if (marchingQueue.isEmpty()) {
            marchingQueue.addAll(terrainGeneration.getMarchingChunksQueue());
        }

        int processed = 0;
        boolean hasAddedChunks = false;

        Iterator<MarchingChunk> it = marchingQueue.iterator();
        while (it.hasNext() && processed < maxChunksPerFrame) {
            MarchingChunk chunk = it.next();
            if (!chunk.isReady) {
                terrainGeneration.requeueChunk(chunk);
                it.remove(); // Remove from marchingQueue
                continue;
            }

            chunk.publishChunk();
            if (!ChunkCoord.compareToVector(chunk.chunkPosition, new Vector3f(0, 0, -10)) &&
                    !ChunkCoord.compareToVector(chunk.chunkPosition, new Vector3f(-1, 0, -10))) {
                terrainGeneration.addSurfaceFeatures(chunk);
            }
            hasAddedChunks = true;

            GameObject chunkEntity = chunk.getChunkEntity();
            if (chunkEntity != null) {
                chunkEntity.getComponent(RenderComponent.class)
                        .getMeshMaterialSets().iterator().next()
                        .material.setShader(ShaderManager.triplanarShader)
                        .setReflectance(64);
                addEntity(chunk.getChunkEntity());
            }
            processed++;
            it.remove(); // Remove from marchingQueue
            if(!it.hasNext()) terrainGeneration.restockQueue();
        }

        if(hasAddedChunks) {
            terrainGeneration.updateSurfaceFeatures();
        }

    }

    @Override
    public void cleanUp() {
        terrainGeneration.end();
        super.cleanUp();
    }
}
