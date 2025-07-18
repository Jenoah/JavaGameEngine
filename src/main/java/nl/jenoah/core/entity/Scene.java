package nl.jenoah.core.entity;

import nl.jenoah.core.ModelManager;
import nl.jenoah.core.MouseInput;
import nl.jenoah.core.WindowManager;
import nl.jenoah.core.components.Component;
import nl.jenoah.core.fonts.fontMeshCreator.FontType;
import nl.jenoah.core.fonts.fontMeshCreator.GUIText;
import nl.jenoah.core.fonts.fontMeshCreator.TextMeshData;
import nl.jenoah.core.gui.GuiObject;
import nl.jenoah.core.lighting.DirectionalLight;
import nl.jenoah.core.lighting.PointLight;
import nl.jenoah.core.lighting.SpotLight;
import org.joml.Vector3f;

import java.util.*;

public class Scene {
    private final List<GameObject> gameObjects;
    private final List<GameObject> rootGameObjects;
    private final List<GuiObject> guiObjects;
    private final Map<FontType, List<GUIText>> textObjects;
    private Vector3f fogColor = new Vector3f(1);
    private float fogDensity = 0.01f;
    private float fogGradient = 15f;

    //Lighting
    private Vector3f ambientLight;
    private PointLight[] pointLights = new PointLight[0];
    private SpotLight[] spotLights = new SpotLight[0];
    private DirectionalLight directionalLight;

    protected final WindowManager windowManager;

    protected String levelName = "Undefined Scene";

    public Scene() {
        this.gameObjects = new ArrayList<>();
        this.rootGameObjects = new ArrayList<>();
        this.guiObjects = new ArrayList<>();
        this.windowManager = WindowManager.getInstance();
        this.textObjects = new HashMap<>();
        init();
    }

    public void init() { }

    public void postStart() { }

    public void update(MouseInput mouseInput) {
        for (GameObject gameObject : gameObjects.stream().toList()) {
            gameObject.update(mouseInput);
        }
    }

    public void handleInput() { }

    public void cleanUp() {
        ModelManager.cleanUp();
        getGameObjects().forEach(gameObject -> gameObject.getComponents().forEach(Component::cleanUp));
    }

    public void addEntity(GameObject entity, boolean intitiateComponents){
        if (entity == null) {
            return;
        }

        if (entity.getChildren() != null) {
            for (GameObject child : entity.getChildren()) {
                addGameObject(child);
            }
        }

        addGameObject(entity);
        if(intitiateComponents && !entity.getComponents().isEmpty()) entity.getComponents().forEach(Component::initiate);
    }

    public void addEntity(GameObject entity) {
        if (entity == null) {
            return;
        }

        if (entity.getChildren() != null) {
            for (GameObject child : entity.getChildren()) {
                addGameObject(child);
            }
        }

        addGameObject(entity);
        if (!entity.getComponents().isEmpty()) entity.getComponents().forEach(Component::initiate);
    }


    public void addGameObject(GameObject gameObject) {
        if (gameObjects.contains(gameObject)) return;

        gameObjects.add(gameObject);
        if(gameObject.getParent() != null) this.rootGameObjects.add(gameObject);

        if (gameObject.getChildren() != null) {
            for (GameObject child : gameObject.getChildren()) {
                addGameObject(child);
            }
        }
    }

    public void removeFromRoot(GameObject gameObject){
        this.rootGameObjects.remove(gameObject);
    }

    public void addGUI(GuiObject guiObject) {
        if (!guiObjects.contains(guiObject)) {
            guiObjects.add(guiObject);

            if (guiObject.getChildren() != null) {
                for (GameObject child : guiObject.getChildren()) {
                    if (child instanceof GuiObject) {
                        addGUI((GuiObject) child);
                    }
                }
            }
        }
    }

    public List<GuiObject> getGuiObjects() {
        return guiObjects;
    }

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public void setAmbientLight(float r, float g, float b) {
        this.ambientLight = new Vector3f(r, g, b);
    }

    public PointLight[] getPointLights() {
        return pointLights;
    }

    public void setPointLights(PointLight[] pointLights) {
        this.pointLights = pointLights;
    }

    public void addPointLight(PointLight pointLight) {
        List<PointLight> pointLightList = new ArrayList<>(Arrays.stream(pointLights).toList());
        pointLightList.add(pointLight);

        this.pointLights = pointLightList.toArray(pointLights);
    }

    public SpotLight[] getSpotLights() {
        return spotLights;
    }

    public void setSpotLights(SpotLight[] spotLights) {
        this.spotLights = spotLights;
    }

    public void addSpotLight(SpotLight spotLight) {
        List<SpotLight> spotLightList = new ArrayList<>(Arrays.stream(spotLights).toList());
        spotLightList.add(spotLight);

        this.spotLights = spotLightList.toArray(spotLights);
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public String getLevelName() {
        return levelName;
    }

    public void addText(GUIText textObject) {
        FontType font = textObject.getFont();
        TextMeshData data = font.loadText(textObject);
        int id = ModelManager.loadModelID(data.getVertexPositions(), data.getTextureCoords(), 2);
        textObject.setMeshInfo(id, data.getVertexCount());
        List<GUIText> textBatch = textObjects.computeIfAbsent(font, k -> new ArrayList<GUIText>());
        textBatch.add(textObject);
    }

    public void updateText(GUIText textObject){
        ModelManager.unloadModel(textObject.getMesh());
        FontType font = textObject.getFont();
        TextMeshData data = font.loadText(textObject);
        int id = ModelManager.loadModelID(data.getVertexPositions(), data.getTextureCoords(), 2);
        textObject.setMeshInfo(id, data.getVertexCount());

        if(!textObjects.get(textObject.getFont()).contains(textObject)){
            List<GUIText> textBatch = textObjects.computeIfAbsent(font, k -> new ArrayList<GUIText>());
            textBatch.add(textObject);
        }
    }

    public void removeText(GUIText textObject) {
        List<GUIText> textBatch = textObjects.get(textObject.getFont());
        textBatch.remove(textObject);
        if (textBatch.isEmpty()) {
            textObjects.remove(textObject.getFont());
            ModelManager.unloadModel(textObject.getMesh());
        }
    }

    public Map<FontType, List<GUIText>> getTextObjects() {
        return textObjects;
    }

    public Vector3f getFogColor() {
        return fogColor;
    }

    public void setFogColor(Vector3f fogColor) {
        this.fogColor = fogColor;
    }

    public float getFogDensity() {
        return fogDensity;
    }

    public float getFogGradient() {
        return fogGradient;
    }

    public void setFogDensity(float fogDensity) {
        this.fogDensity = fogDensity;
    }

    public void setFogGradient(float fogGradient) {
        this.fogGradient = fogGradient;
    }

    public void disableFog(){
        this.fogGradient = 100000;
        this.fogDensity = 0;
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public List<GameObject> getRootGameObjects() {
        return gameObjects;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public GameObject getGameObjectByGUID(String guid){
        return gameObjects.stream().filter(go -> guid.equals(go.getGuid())).findAny().orElse(null);
    }
}
