package nl.jenoah.core.lighting;

import org.joml.Vector3f;

public class PointLight extends Light {

    public PointLight() { super(); }

    public PointLight(Vector3f color, Vector3f position, float intensity, float constant, float linear, float exponent) {
        super(color, position, intensity, constant, linear, exponent);
    }

    public PointLight(Vector3f color, Vector3f position, float intensity, float distance) {
        super(color, position, intensity, distance);
    }




}
