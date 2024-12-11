package nl.jenoah.core.utils;

import org.joml.Vector3f;
import org.joml.Vector3i;

public class Calculus {

    public static Vector3f addVectors(Vector3f a, Vector3f b){
        return new Vector3f(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vector3f subtractVectors(Vector3f a, Vector3f b){
        return new Vector3f(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static Vector3f multiplyVector(Vector3f a, float b){
        return new Vector3f(a.x * b, a.y * b, a.z * b);
    }

    public static Vector3i addVectors(Vector3i a, Vector3i b){
        return new Vector3i(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vector3i subtractVectors(Vector3i a, Vector3i b){
        return new Vector3i(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static Vector3i multiplyVector(Vector3i a, float b){
        return new Vector3i((int)(a.x * b), (int)(a.y * b), (int)(a.z * b));
    }
}
