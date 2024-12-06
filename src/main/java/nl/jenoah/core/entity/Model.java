package nl.jenoah.core.entity;

import game.Launcher;
import nl.jenoah.core.ModelManager;
import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.shaders.ShaderManager;

public class Model {

    private int id;
    private int vertexCount;
    private boolean isDoubleSided = false;
    private Material material;

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
            this.material.setTexture(new Texture(ModelManager.getInstance().getTextureLoader().loadTexture(texturePath)));
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
}
