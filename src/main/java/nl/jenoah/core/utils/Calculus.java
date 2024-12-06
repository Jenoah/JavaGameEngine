package nl.jenoah.core.utils;

import org.joml.Vector3f;

public class Calculus {

    public static Vector3f addVectors(Vector3f a, Vector3f b){
        return new Vector3f(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vector3f subtractVectors(Vector3f a, Vector3f b){
        return new Vector3f(a.x - b.x, a.y - b.y, a.z - b.z);
    }
}
