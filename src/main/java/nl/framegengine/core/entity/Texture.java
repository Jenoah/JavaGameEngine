package nl.framegengine.core.entity;

import nl.framegengine.core.loaders.TextureLoader;

public class Texture {

    private final int id;

    public int getId() {
        return id;
    }

    public Texture(int id) {
        this.id = id;
    }

    public Texture(String texturePath){
        this.id = TextureLoader.loadTexture(texturePath);
    }

    public Texture(String texturePath, boolean pointFilter){
        this.id = TextureLoader.loadTexture(texturePath, pointFilter);
    }

    public Texture(String texturePath, boolean pointFilter, boolean flipped){
        this.id = TextureLoader.loadTexture(texturePath, pointFilter, flipped);
    }

    public Texture(String texturePath, boolean pointFilter, boolean flipped, boolean repeat, boolean isNormalMap){
        this.id = TextureLoader.loadTexture(texturePath, pointFilter, flipped, repeat, isNormalMap);
    }
}
