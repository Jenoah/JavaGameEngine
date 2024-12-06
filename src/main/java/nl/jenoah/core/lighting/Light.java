package nl.jenoah.core.lighting;

import nl.jenoah.core.ModelManager;
import nl.jenoah.core.entity.*;
import nl.jenoah.core.shaders.ShaderManager;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Light extends GameObject {

    protected Vector3f color;
    protected float intensity;
    protected float constant;
    protected float linear;
    protected float exponent;
    protected Entity proxy;

    public Light(Vector3f color, Vector3f position, float intensity, float constant, float linear, float exponent) {
        this.color = color;
        this.intensity = intensity;
        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;

        setPosition(position);
    }

    public Light(Vector3f color, Vector3f position, float intensity, float distance) {
        this(color, position, intensity, 1, 0, 0);
        this.color = color;
        this.intensity = intensity;
        setValuesByDistance(distance);
        this.setPosition(position);
    }

    public void setValuesByDistance(float distance){
        float distancePlusOne = distance + 1;

        this.constant = 1.0f; // Always constant
        this.linear = 1f / distancePlusOne;
        this.exponent = 1f / (distancePlusOne * distancePlusOne);
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getExponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

    public void showProxy(){
        if(proxy == null){
            Model lightProxyModel = new Model(ModelManager.getInstance().getPrimitiveLoader().getQuad(), "textures/lightDirection.png");
            Material proxyMaterial = lightProxyModel.getMaterial();
            proxyMaterial.setShader(ShaderManager.getInstance().getUnlitShader());
            proxyMaterial.setAmbientColor(new Vector4f(color, 1));
            proxyMaterial.setReflectance(0);
            lightProxyModel.setDoubleSided(true);
            Entity proxy = new Entity(lightProxyModel, new Vector3f(0), new Vector3f(0, 90, 0), 1, true);
            proxy.setParent(this);
        }
    }
}
