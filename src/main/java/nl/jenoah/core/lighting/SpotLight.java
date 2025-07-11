package nl.jenoah.core.lighting;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpotLight extends Light {

    private Vector3f coneDirection;

    private float cutOff;
    private float outerCutOff;

    public SpotLight(Vector3f color, Vector3f position, float intensity, float constant, float linear, float exponent, Quaternionf coneDirection, float cutOff, float outerCutOff) {
        super(color, position, intensity, constant, linear, exponent);

        Vector3f coneDirectionEuler = new Vector3f(0);
        coneDirection.getEulerAnglesXYZ(coneDirectionEuler);

        this.coneDirection = coneDirectionEuler;
        this.cutOff = cutOff;
        this.outerCutOff = outerCutOff;

        setPosition(position);
    }

    public SpotLight(Vector3f color, Vector3f position, float intensity, float constant, float linear, float exponent, Vector3f coneDirection, float cutOff, float outerCutOff) {
        super(color, position, intensity, constant, linear, exponent);
        this.coneDirection = coneDirection;
        this.cutOff = cutOff;
        this.outerCutOff = outerCutOff;

        setPosition(position);
    }

    public SpotLight(Vector3f color, Vector3f position, float intensity, float distance, Vector3f coneDirection, float cutOff, float outerCutOff) {
        super(color, position, intensity, distance);
        this.coneDirection = coneDirection;
        this.cutOff = cutOff;
        this.outerCutOff = outerCutOff;

        setPosition(position);
    }

    public Vector3f getConeDirection() {
        return this.coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
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
