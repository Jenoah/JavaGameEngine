package nl.jenoah.core.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Constants {

    public static final String TITLE = "Java Game Engine";

    public static final float FOV = (float) Math.toRadians(60);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;
    public static final float GAMMA = 2.2f;

    public static final float SPECULAR_POWER = .5f;
    public static final float CAMERA_MOVE_SPEED = 0.025f;
    public static final float MOUSE_SENSITIVITY = 0.02f;


    public static final int CHUNK_SIZE = 16;

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1f, 1f, 1f, 1f);
    public static final Vector3f AMBIENT_COLOR = new Vector3f(0.4f, 0.4f, 0.6f);

    public static final float DEGREES_90_IN_RADIANS = 1.57f;
}
