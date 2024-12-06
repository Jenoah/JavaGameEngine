package nl.jenoah.core;

import nl.jenoah.core.entity.GameObject;
import nl.jenoah.core.utils.Constants;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends GameObject {

    public Camera() {
        super();
        setPosition(new Vector3f(0, 0, 0));
    }

    public Camera(Vector3f position, Vector3f rotation) {
        setPosition(position);
        setRotation(rotation);
    }

    public Camera(Vector3f position, Quaternionf rotation) {
        setPosition(position);
        setRotation(rotation);
    }

}
