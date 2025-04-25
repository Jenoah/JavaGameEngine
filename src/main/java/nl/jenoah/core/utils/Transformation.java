package nl.jenoah.core.utils;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.GameObject;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transformation {

    public static Matrix4f getModelMatrix(GameObject gameObject){
        return new Matrix4f()
                .identity() // Start with an identity matrix
                .translate(gameObject.getPosition()) // Translate to the entity's position
                .rotate(gameObject.getRotation())
                .scale(gameObject.getScale());
    }

    public static Matrix4f getViewMatrix(Camera camera){
        return new Matrix4f()
                .identity() // Start with an identity matrix
                .rotate(camera.getRotation())
                .translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
    }

    public static Vector3f rotateDirection(Vector3f input, Vector3f rotation){
        float yaw = (float) Math.toRadians(rotation.y); // Yaw (rotation around Y-axis)
        float pitch = (float) Math.toRadians(rotation.x); // Pitch (rotation around X-axis)
        float roll = (float) Math.toRadians(rotation.z); // Roll (rotation around Z-axis)

        // Create a quaternion from the Euler angles
        Quaternionf quaternion = new Quaternionf()
                .rotateYXZ(yaw, pitch, roll);

        // Rotate the input vector
        Vector3f rotatedDirection = new Vector3f(input); // Copy the input vector
        quaternion.transform(rotatedDirection); // Apply the rotation

        return rotatedDirection; // Return the rotated direction
    }
}
