package nl.jenoah.core.entity;

import game.entities.Player;
import nl.jenoah.core.ModelManager;
import nl.jenoah.core.MouseInput;
import nl.jenoah.core.WindowManager;
import nl.jenoah.core.debugging.Debug;
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
    private final HashMap<Material, List<Entity>> transparentEntities;
    private final HashMap<Material, List<Entity>> solidEntities;
    private final List<GameObject> gameObjects;
    private final List<GuiObject> guiObjects;
    private final Map<FontType, List<GUIText>> textObjects;
    private Vector3f fogColor = new Vector3f(1);
    private float fogDensity = 0.01f;
    private float fogGradient = 15f;

    protected Player player;

    //Lighting
    private Vector3f ambientLight;
    private PointLight[] pointLights = new PointLight[0];
    private SpotLight[] spotLights = new SpotLight[0];
    private DirectionalLight directionalLight;

    protected final WindowManager windowManager;

    protected String levelName = "Undefined Scene";

    public Scene(){
        this.transparentEntities = new HashMap<>();
        this.solidEntities = new HashMap<>();
        this.gameObjects = new ArrayList<>();
        this.guiObjects = new ArrayList<>();
        this.windowManager = WindowManager.getInstance();
        this.textObjects = new HashMap<>();

        this.player = new Player();
        init();
    }

    public void init() { }

    public void postStart(){ }

    public void update(MouseInput mouseInput){
        for(GameObject gameObject: gameObjects){
            gameObject.update(mouseInput);
        }
    }

    public void handleInput(){
        //Debug.Log("" + mouseInput.getMousePositionInPixels());
    }

    public void cleanUp(){
        ModelManager.cleanUp();
    }

    public HashMap<Material, List<Entity>> getTransparentEntities() {
        return transparentEntities;
    }

    public HashMap<Material, List<Entity>> getSolidEntities() {
        return solidEntities;
    }

    public void addEntity(Entity entity){
        if (entity == null || transparentEntities.containsValue(entity) || solidEntities.containsValue(entity)) {
            return;
        }

        if (entity.isTransparent()) {
            addToEntityList(transparentEntities, entity);
        } else {
            //addToEntityList(solidEntities, entity);
            Material mat = entity.getModel().getMaterial();

            if(solidEntities.containsKey(mat)){
                solidEntities.get(mat).add(entity);
            }else{
                solidEntities.put(mat, new ArrayList<>(){{add(entity);}});
            }
        }


        if (entity.getChildren() != null) {
            for (GameObject child : entity.getChildren()) {
                if (child instanceof Entity) {
                    addEntity((Entity) child);
                    addGameObject(child);
                    Debug.Log("Adding " + child);
                }
            }
        }

        addGameObject(entity);
    }

    private void addToEntityList(HashMap<Material, List<Entity>> entityList, Entity entity){
        Material mat = entity.getModel().getMaterial();

        if(entityList.containsKey(mat)){
            entityList.get(mat).add(entity);
        }else{
            entityList.put(mat, new ArrayList<>(){{add(entity);}});
        }
    }

    public void addGameObject(GameObject gameObject){
        if(!gameObjects.contains(gameObject)){
            gameObjects.add(gameObject);

            if (gameObject.getChildren() != null) {
                for (GameObject child : gameObject.getChildren()) {
                    if (child instanceof Entity) {
                        addEntity((Entity) child);
                        addGameObject(child);
                    }
                }
            }
        }
    }

    public void addGUI(GuiObject guiObject){
        if(!guiObjects.contains(guiObject)){
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

    public List<GuiObject> getGuiObjects(){
        return guiObjects;
    }

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public void setAmbientLight(float r, float g, float b){
        this.ambientLight = new Vector3f(r, g, b);
    }

    public PointLight[] getPointLights() {
        return pointLights;
    }

    public void setPointLights(PointLight[] pointLights) {
        this.pointLights = pointLights;
    }

    public void addPointLight(PointLight pointLight){
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

    public void addSpotLight(SpotLight spotLight){
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

    public String getLevelName(){
        return levelName;
    }

    public Player getPlayer(){
        return player;
    }

    public void addText(GUIText textObject){
        FontType font = textObject.getFont();
        TextMeshData data = font.loadText(textObject);
        int id = ModelManager.loadModelID(data.getVertexPositions(), data.getTextureCoords());
        textObject.setMeshInfo(id, data.getVertexCount());
        List<GUIText> textBatch = textObjects.computeIfAbsent(font, k -> new ArrayList<GUIText>());
        textBatch.add(textObject);
    }

    public void removeText(GUIText textObject){
        List<GUIText> textBatch = textObjects.get(textObject.getFont());
        textBatch.remove(textObject);
        if(textBatch.isEmpty()){
            textObjects.remove(textObject.getFont());
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

    public void setFogGradient(float fogGradient){
        this.fogGradient = fogGradient;
    }
}
