package nl.framegengine.core.entity;

import nl.framegengine.editor.EngineSettings;
import nl.framegengine.core.components.Component;
import nl.framegengine.core.components.RenderComponent;
import nl.framegengine.core.debugging.Debug;
import nl.framegengine.core.lighting.DirectionalLight;
import nl.framegengine.core.lighting.Light;
import nl.framegengine.core.lighting.PointLight;
import nl.framegengine.core.lighting.SpotLight;
import nl.framegengine.core.loaders.OBJLoader.OBJLoader;
import nl.framegengine.core.rendering.MeshMaterialSet;
import nl.framegengine.core.shaders.ShaderManager;
import nl.framegengine.core.components.ComponentLoader;
import nl.framegengine.core.utils.JsonHelper;
import org.joml.Vector3f;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.net.URL;
import java.util.*;

public class SceneManager {
    private List<Scene> scenes = new ArrayList<>();
    private Scene currentScene = null;
    public static Vector3f fogColor = new Vector3f(1);
    public static float fogDensity = 0.01f;
    public static float fogGradient = 15f;

    private static SceneManager instance = null;
    private ComponentLoader componentLoader;

    public static synchronized SceneManager getInstance()
    {
        if (instance == null) {
            instance = new SceneManager();
        }

        return instance;
    }

    public Scene loadScene(String filePath) throws Exception {
        Scene newScene = new Scene();

        if(componentLoader == null){
            URL inputResourceUrl = new File(EngineSettings.currentProjectDirectory).toURI().toURL();
            URL compiledResourceUrl = new File(EngineSettings.currentProjectDirectory + File.separator + "/.compiled").toURI().toURL();

            componentLoader = new ComponentLoader(inputResourceUrl.toURI().getPath(), compiledResourceUrl.toURI().getPath());
        }

        try (InputStream is = new FileInputStream(filePath);
             JsonReader reader = Json.createReader(is)) {
            JsonObject sceneInfo = reader.readObject();

            JsonHelper.loadVariableIntoObject(newScene, sceneInfo);

            // Game Objects
            sceneInfo.getJsonArray("gameObjects").forEach(goInfoContainer -> {
                JsonObject goInfo = goInfoContainer.asJsonObject();
                String goTypeName = "GameObject";
                if(JsonHelper.hasJsonKey(goInfo, "goType")) goTypeName = goInfo.getString("goType");
                GoType goType = GoType.fromJsonName(goTypeName);

                GameObject go = null;
                try {
                    go = goType.createInstance();
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }

                JsonHelper.loadVariableIntoObject(go, goInfo, new String[]{"parentGuid", "goType", "meshPath", "texturePath", "isMain"});

                if (JsonHelper.hasJsonKey(goInfo, "meshPath")) {
                    Set<MeshMaterialSet> meshMaterialSets = OBJLoader.loadOBJModel(goInfo.getString("meshPath"));

                    if(JsonHelper.hasJsonKey(goInfo, "texturePath")){
                        JsonObject textureInfo = goInfo.getJsonObject("texturePath");
                        if(!JsonHelper.hasJsonKey(textureInfo, "diffuse")) return;
                        Material meshMaterial = new Material(ShaderManager.pbrShader);
                        meshMaterial.setAlbedoTexture(new Texture(EngineSettings.currentProjectDirectory + File.separator + textureInfo.getString("diffuse")));
                        if(JsonHelper.hasJsonKey(textureInfo, "normal")) meshMaterial.setNormalMap(new Texture(EngineSettings.currentProjectDirectory + File.separator + textureInfo.getString("normal"), false, false, true, true));
                        if(JsonHelper.hasJsonKey(textureInfo, "roughness")) meshMaterial.setRoughnessMap(new Texture(EngineSettings.currentProjectDirectory + File.separator + textureInfo.getString("roughness"), false, false, true, false));
                        meshMaterial.setRoughness(.6f);
                        meshMaterialSets.forEach(meshMaterialSet -> meshMaterialSet.material = meshMaterial);
                    }

                    RenderComponent renderComponent = new RenderComponent(meshMaterialSets);
                    go.addComponent(renderComponent);
                }
                switch (goType){
                    case GoType.DIRECTIONAL_LIGHT:
                    case GoType.POINT_LIGHT:
                    case GoType.SPOT_LIGHT:
                        tryAddLight((Light) go, newScene);
                        break;
                    case GoType.CAMERA:
                        if(JsonHelper.hasJsonKey(goInfo, "isMain") && goInfo.getBoolean("isMain")) ((Camera)go).setAsMain();
                        break;
                }

                if(JsonHelper.hasJsonKey(goInfo, "parentGuid")) go.setParent(newScene.getGameObjectByGUID(goInfo.getString("parentGuid")));

                tryAddComponent(goInfo, go);
                newScene.addEntity(go, false);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        newScene.getGameObjects().forEach(go -> go.getComponents().forEach(Component::initiate));

        newScene.updateLights();
        return newScene;
    }

    private void tryAddLight(Light lightObject, Scene scene){
        switch (lightObject) {
            case DirectionalLight directionalLight -> {
                scene.setDirectionalLight(directionalLight);
                directionalLight.showProxy();
            }
            case SpotLight spotLight -> {
                spotLight.showProxy();
                scene.addSpotLight(spotLight);
            }
            case PointLight pointLight -> {
                pointLight.showProxy();
                scene.addPointLight(pointLight);
            }
            default -> throw new IllegalStateException("Unexpected value: " + lightObject);
        }
    }

    private void tryAddComponent(JsonObject jsonObject, GameObject gameObject){
        if(JsonHelper.hasJsonKey(jsonObject, "components")){
            jsonObject.getJsonArray("components").forEach(componentInfoContainer -> {
                JsonObject componentInfo = componentInfoContainer.asJsonObject();
                if(! JsonHelper.hasJsonKey(componentInfo, "class")) return;
                String className = componentInfo.getString("class");

                try {
                    Component component = componentLoader.loadComponent("nl.framegengine.customScripts." + className);
                    if(component != null) {

                        JsonHelper.loadVariableIntoObject(component, componentInfo, new String[]{"class"});

                        component.setRoot(gameObject);
                        gameObject.addComponent(component);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public void setScenes(List<Scene> scenes) {
        this.scenes = scenes;
    }

    public void addScene(Scene scene){
        this.scenes.add(scene);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(int sceneIndex) {
        this.currentScene = scenes.get(sceneIndex);
        fogColor = currentScene.getFogColor();
        fogDensity = currentScene.getFogDensity();
        fogGradient = currentScene.getFogGradient();
        Debug.Log("Loading " + currentScene.getLevelName());
    }

    public void cleanUp(){
        currentScene.cleanUp();
        instance = null;
    }

    public enum GoType{
        GAME_OBJECT("GameObject", GameObject.class),
        CAMERA("Camera", Camera.class),
        DIRECTIONAL_LIGHT("DirectionalLight", DirectionalLight.class),
        POINT_LIGHT("PointLight", PointLight.class),
        SPOT_LIGHT("SpotLight", SpotLight.class);

        private final String jsonName;
        private final Class<? extends GameObject> clazz;

        GoType(String jsonName, Class<? extends GameObject> clazz) {
            this.jsonName = jsonName;
            this.clazz = clazz;
        }

        public static GoType fromJsonName(String name) {
            for (GoType type : values()) {
                if (type.jsonName.equals(name)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown classType: " + name);
        }

        public GameObject createInstance() throws ReflectiveOperationException {
            return clazz.getDeclaredConstructor().newInstance();
        }
    }
}
