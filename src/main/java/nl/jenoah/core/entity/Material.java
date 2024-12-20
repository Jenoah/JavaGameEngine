package nl.jenoah.core.entity;

import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Constants;
import org.joml.Vector4f;

public class Material {

    private Vector4f ambientColor;
    private Vector4f diffuseColor;
    private Vector4f specularColor;

    private float reflectance = 32;
    private Texture texture;

    private Shader shader;

    public Material(Shader shader){
        this.ambientColor = Constants.DEFAULT_COLOR;
        this.diffuseColor = Constants.DEFAULT_COLOR;
        this.specularColor = Constants.DEFAULT_COLOR;
        this.texture = null;
        this.shader = shader;
    }

    public Material(Shader shader, Vector4f color, float reflectance){
        this(shader, color, color, color, reflectance, null);
    }

    public Material(Shader shader, Vector4f color, float reflectance, Texture texture){
        this(shader, color, color, color, reflectance, texture);
    }

    public Material(Shader shader, Texture texture){
        this(shader, Constants.DEFAULT_COLOR, Constants.DEFAULT_COLOR, Constants.DEFAULT_COLOR, 0, texture);
    }

    public Material(Shader shader, Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance, Texture texture) {
        this.shader = shader;
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.reflectance = reflectance;
        this.texture = texture;
    }

    public Vector4f getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public Vector4f getSpecularColor() {
        return specularColor;
    }

    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean hasTexture(){
        return texture != null;
    }

    public Shader getShader(){
        return shader;
    }

    public void setShader(Shader shader){
        this.shader = shader;
    }
}
