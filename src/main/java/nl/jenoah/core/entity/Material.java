package nl.jenoah.core.entity;

import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Constants;
import org.joml.Vector4f;

public class Material {

    private Vector4f ambientColor = Constants.DEFAULT_COLOR;
    private Vector4f diffuseColor = Constants.DEFAULT_COLOR;
    private Vector4f specularColor = Constants.DEFAULT_COLOR;

    private float reflectance = 32;
    private Texture albedoTexture = null;
    private Texture normalMap = null;

    private boolean isDoubleSided = false;

    private Shader shader;

    public Material(Shader shader){
        this.shader = shader;
    }

    public Material(Material material){
        this.shader = material.getShader();
        this.ambientColor = material.getAmbientColor();
        this.diffuseColor = material.getDiffuseColor();
        this.specularColor = material.getSpecularColor();
        this.reflectance = material.getReflectance();
        this.albedoTexture = material.getAlbedoTexture();
        this.normalMap = material.getNormalMap();
        this.isDoubleSided = material.isDoubleSided();
    }

    public Material(Shader shader, Texture albedoTexture){
        this(shader);
        this.setAlbedoTexture(albedoTexture);
    }

//      Setters

    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public void setAlbedoTexture(Texture texture) {
        this.albedoTexture = texture;
    }

    public void setNormalMap(Texture texture) {
        this.normalMap = texture;
    }

    public void setShader(Shader shader){
        this.shader = shader;
    }

    public void setDoubleSided(boolean isDoubleSided){
        this.isDoubleSided = isDoubleSided;
    }

//    Getters

    public final Vector4f getAmbientColor() {
        return ambientColor;
    }

    public final Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public final Vector4f getSpecularColor() {
        return specularColor;
    }

    public final float getReflectance() {
        return reflectance;
    }

    public final Texture getAlbedoTexture() {
        return albedoTexture;
    }

    public final Texture getNormalMap() {
        return normalMap;
    }

    public final Shader getShader(){
        return shader;
    }

    public final boolean isDoubleSided(){
        return isDoubleSided;
    }

//    Has Getters

    public final boolean hasAlbedoTexture(){
        return albedoTexture != null;
    }

    public final boolean hasNormalMap(){
        return normalMap != null;
    }
}
