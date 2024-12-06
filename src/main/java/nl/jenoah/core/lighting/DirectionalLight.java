package nl.jenoah.core.lighting;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DirectionalLight extends Light{

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        super(color, new Vector3f(0), intensity, 0);
        setDirection(direction);
    }

    public Vector3f getDirection() {
        Quaternionf rotation = getRotation();

        Vector3f forward = new Vector3f(
                2 * (rotation.x * rotation.z + rotation.w * rotation.y),
                2 * (rotation.y * rotation.z - rotation.w * rotation.x),
                1 - 2 * (rotation.x * rotation.x + rotation.y * rotation.y)
        );

        forward.normalize();

        return forward;
    }

    public void setDirection(Vector3f direction) {
        direction.normalize();

        setRotation(new Quaternionf().rotateTo(new Vector3f(0,0,1), direction));
    }
}
