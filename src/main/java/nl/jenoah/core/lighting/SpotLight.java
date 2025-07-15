package nl.jenoah.core.lighting;

import org.joml.Vector3f;

public class SpotLight extends Light {

    private float cutOff;
    private float outerCutOff;

    public SpotLight(){ super(); }

    public SpotLight(Vector3f color, Vector3f position, float intensity, float constant, float linear, float exponent, float cutOff, float outerCutOff) {
        super(color, position, intensity, constant, linear, exponent);

        this.cutOff = cutOff;
        this.outerCutOff = outerCutOff;

        setPosition(position);
    }

    public SpotLight(Vector3f color, Vector3f position, float intensity, float distance, float cutOff, float outerCutOff) {
        super(color, position, intensity, distance);
        this.cutOff = cutOff;
        this.outerCutOff = outerCutOff;

        setPosition(position);
    }

    public float getCutOff() {
        return this.cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }

    public float getOuterCutOff() {
        return this.outerCutOff;
    }

    public void setOuterCutOff(float outerCutOff) {
        this.outerCutOff = outerCutOff;
    }
}
