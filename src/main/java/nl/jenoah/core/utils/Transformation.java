package nl.jenoah.core.utils;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.GameObject;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transformation {

    public static Matrix4f getModelMatrix(GameObject gameObject){
        return new Matrix4f()
                .identity()
                .translate(gameObject.getPosition())
                .rotate(gameObject.getRotation())
                .scale(gameObject.getScale());
    }

    public static Matrix4f toModelMatrix(Vector3f position, Quaternionf rotation, Vector3f scale){
        return new Matrix4f()
                .identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
    }

    public static Matrix4f getViewMatrix(Camera camera){
        return new Matrix4f()
                .identity()
                .rotate(camera.getRotation())
                .translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
    }

    public static Vector3f rotateDirection(Vector3f input, Vector3f rotation){
        float yaw = (float) Math.toRadians(rotation.y);
        float pitch = (float) Math.toRadians(rotation.x);
        float roll = (float) Math.toRadians(rotation.z);

        Quaternionf quaternion = new Quaternionf()
                .rotateYXZ(yaw, pitch, roll);

        Vector3f rotatedDirection = new Vector3f(input);
        quaternion.transform(rotatedDirection);

        return rotatedDirection;
    }
}
