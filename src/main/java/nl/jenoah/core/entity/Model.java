package nl.jenoah.core.entity;

import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.loaders.TextureLoader;
import nl.jenoah.core.shaders.ShaderManager;
import nl.jenoah.core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import java.nio.FloatBuffer;

public class Model {

    private final int id;
    private final int vertexCount;
    private boolean isDoubleSided = false;
    private Material material;
    private float[] textureCoords;
    private int textureCoordVBOID = -1;

    public Model(int id, int vertexCount){
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material(ShaderManager.getInstance().getLitShader());
    }

    public Model(int id, int vertexCount, Texture texture){
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material(ShaderManager.getInstance().getLitShader(), texture);
    }

    public Model(Model model, Texture texture){
        this.id = model.getId();
        this.vertexCount = model.getVertexCount();
        this.material = model.getMaterial();
        this.material.setTexture(texture);
    }

    public Model(Model model, String texturePath){
        this.id = model.getId();
        this.vertexCount = model.getVertexCount();
        this.material = model.getMaterial();

        if(!texturePath.isEmpty()){
            this.material.setTexture(new Texture(TextureLoader.loadTexture(texturePath)));
        }
    }

    public int getId() {
        return id;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Texture getTexture(){
        return material.getTexture();
    }

    public void setTexture(Texture texture){
        this.material.setTexture(texture);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setTexture(Texture texture, float reflectance){
        this.material.setTexture(texture);
        this.material.setReflectance(reflectance);
    }

    public void setDoubleSided(boolean isDoubleSided){
        this.isDoubleSided = isDoubleSided;
    }

    public boolean isDoubleSided() {
        return isDoubleSided;
    }

    public void setTextureScale(float textureScale){
        if(textureCoords == null || textureCoordVBOID == -1){
            Debug.Log("Texture coordinates not set");
            return;
        }

        float[] updatedTextureCoords = textureCoords;
        for (int i = 0; i < updatedTextureCoords.length; i++) {
            updatedTextureCoords[i] *= textureScale;
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textureCoordVBOID);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(updatedTextureCoords);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void setTextureCoordinates(int VBOID, float[] textureCoordinates){
        this.textureCoordVBOID = VBOID;
        this.textureCoords = textureCoordinates;
    }
}
