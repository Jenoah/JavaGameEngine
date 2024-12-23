package nl.jenoah.core.entity;

import nl.jenoah.core.loaders.TextureLoader;
import nl.jenoah.core.shaders.ShaderManager;

public class Model {
    private Material material;
    private final Mesh mesh;

    public Model(Mesh mesh){
        this.mesh = mesh;
        //this.material = new Material(ShaderManager.pbrShader);
        this.material = new Material(ShaderManager.litShader);
    }

    public Model(Model model, Texture texture){
        this.mesh = model.getMesh();
        this.material = new Material(model.getMaterial());
        this.material.setAlbedoTexture(texture);
    }

    public Model(Model model, String texturePath){
        this.mesh = model.getMesh();
        this.material = new Material(model.getMaterial());

        if(!texturePath.isEmpty()){
            this.material.setAlbedoTexture(new Texture(TextureLoader.loadTexture(texturePath)));
        }
    }

    public int getId() {
        return mesh.getVaoID();
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Mesh getMesh(){
        return mesh;
    }
}
