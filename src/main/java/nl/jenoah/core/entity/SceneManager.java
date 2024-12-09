package nl.jenoah.core.entity;

import nl.jenoah.core.debugging.Debug;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {
    private List<Scene> scenes = new ArrayList<>();
    private Scene currentScene = null;
    public static Vector3f fogColor = new Vector3f(1);
    public static float fogDensity = 0.01f;
    public static float fogGradient = 15f;

    private static SceneManager instance = null;

    public static synchronized SceneManager getInstance()
    {
        if (instance == null) {
            instance = new SceneManager();
        }

        return instance;
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
    }
}
