package nl.jenoah.core.lighting;

import nl.jenoah.core.components.RenderComponent;
import nl.jenoah.core.entity.*;
import nl.jenoah.core.loaders.PrimitiveLoader;
import nl.jenoah.core.shaders.ShaderManager;
import org.joml.Vector3f;

public class Light extends GameObject {

    protected Vector3f color;
    protected float intensity;
    protected float constant;
    protected float linear;
    protected float exponent;
    protected GameObject proxy;

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

    public GameObject showProxy(){
        if(proxy == null){
            Material proxyMaterial = new Material(ShaderManager.unlitShader);
            proxyMaterial.setAlbedoTexture(new Texture("textures/lightDirection.png"));
            proxyMaterial.setDoubleSided(true);
            proxy = new GameObject().setRotation(new Vector3f(0f, 90f, 0f));
            RenderComponent renderComponent = new RenderComponent(PrimitiveLoader.getQuad().getMesh(), proxyMaterial);
            proxy.addComponent(renderComponent);
            renderComponent.initiate();
            proxy.setParent(this);

            return proxy;
        }

        return proxy;
    }
}
