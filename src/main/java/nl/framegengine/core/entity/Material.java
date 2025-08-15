package nl.framegengine.core.entity;

import nl.framegengine.core.IJsonSerializable;
import nl.framegengine.core.debugging.Debug;
import nl.framegengine.core.shaders.Shader;
import nl.framegengine.core.utils.Constants;
import nl.framegengine.core.utils.JsonHelper;
import org.joml.Math;
import org.joml.Vector4f;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

public class Material implements IJsonSerializable {

    protected Vector4f ambientColor = Constants.DEFAULT_COLOR;
    protected Vector4f diffuseColor = Constants.DEFAULT_COLOR;
    protected Vector4f specularColor = Constants.DEFAULT_COLOR;

    protected float reflectance = 0.04f;
    protected float roughness = 0.1f;
    protected Texture albedoTexture = null;
    protected Texture normalMap = null;
    protected Texture roughnessMap = null;
    protected Texture metallicMap = null;
    protected Texture aoMap = null;

    protected boolean isDoubleSided = false;
    protected boolean castShadow = true;
    protected boolean receiveShadows = true;
    protected boolean isTransparent;

    protected Shader shader;

    public Material(){}

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

    public Material setTransparent(boolean isTransparent){
        this.isTransparent = isTransparent;
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

    public final boolean isTransparent() { return isTransparent; }

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

    @Override
    public JsonObject serializeToJson() {
        return JsonHelper.objectToJson(this);
    }

    @Override
    public IJsonSerializable deserializeFromJson(String json) {
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonInfo = jsonReader.readObject();
        try{
            JsonHelper.loadVariableIntoObject(this, jsonInfo);
        } catch (Exception e) {
            Debug.LogError("Error loading in data: " + e.getMessage());
        }
        return this;
    }
}
