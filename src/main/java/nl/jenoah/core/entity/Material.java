package nl.jenoah.core.entity;

import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Constants;
import org.joml.Math;
import org.joml.Vector4f;

public class Material {

    private Vector4f ambientColor = Constants.DEFAULT_COLOR;
    private Vector4f diffuseColor = Constants.DEFAULT_COLOR;
    private Vector4f specularColor = Constants.DEFAULT_COLOR;

    private float reflectance = 0.04f;
    private float roughness = 0.1f;
    private Texture albedoTexture = null;
    private Texture normalMap = null;
    private Texture roughnessMap = null;
    private Texture metallicMap = null;
    private Texture aoMap = null;

    private boolean isDoubleSided = false;
    private boolean castShadow = true;
    private boolean receiveShadows = true;

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
        this.roughnessMap = material.getRoughnessMap();
        this.metallicMap = material.getMetallicMap();
        this.aoMap = material.getAoMap();
        this.roughness = material.getRoughness();
    }

    public Material(Shader shader, Texture albedoTexture){
        this(shader);
        this.setAlbedoTexture(albedoTexture);
    }

//      Setters

    public Material setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
        return this;
    }

    public Material setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
        return this;
    }

    public Material setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
        return this;
    }

    public Material setReflectance(float reflectance) {
        this.reflectance = reflectance;
        return this;
    }

    public Material setRoughness(float roughness){
        this.roughness = Math.clamp(0.01f, 1f, roughness);
        return this;
    }

    public Material setAlbedoTexture(Texture texture) {
        this.albedoTexture = texture;
        return this;
    }

    public Material setNormalMap(Texture texture) {
        this.normalMap = texture;
        return this;
    }

    public Material setRoughnessMap(Texture texture) {
        this.roughnessMap = texture;
        return this;
    }

    public Material setMetallicMap(Texture texture) {
        this.metallicMap = texture;
        return this;
    }

    public Material setAOMap(Texture texture) {
        this.aoMap = texture;
        return this;
    }

    public Material setShader(Shader shader){
        this.shader = shader;
        return this;
    }

    public Material setDoubleSided(boolean isDoubleSided){
        this.isDoubleSided = isDoubleSided;
        return this;
    }

    public Material castShadow(boolean canCast){
        castShadow = canCast;
        return this;
    }

    public Material receiveShadows(boolean canReceive){
        receiveShadows = canReceive;
        return this;
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

    public final float getRoughness(){
        return roughness;
    }

    public final Texture getAlbedoTexture() {
        return albedoTexture;
    }

    public final Texture getNormalMap() {
        return normalMap;
    }

    public final Texture getRoughnessMap() {
        return roughnessMap;
    }

    public final Texture getMetallicMap() {
        return metallicMap;
    }

    public final Texture getAoMap() {
        return aoMap;
    }

    public final Shader getShader(){
        return shader;
    }

    public final boolean isDoubleSided(){
        return isDoubleSided;
    }

    public final boolean castShadow(){ return castShadow; }

    public final boolean receiveShadows(){ return receiveShadows; }

//  Has Getters

    public final boolean hasAlbedoTexture(){
        return albedoTexture != null;
    }

    public final boolean hasNormalMap(){
        return normalMap != null;
    }

    public final boolean hasRoughnessMap(){
        return roughnessMap != null;
    }

    public final boolean hasMetallicMap(){
        return metallicMap != null;
    }

    public final boolean hasAOMap(){
        return aoMap != null;
    }
}
