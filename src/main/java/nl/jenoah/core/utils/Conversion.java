package nl.jenoah.core.utils;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.List;

public class Conversion {

    public static float[] v2ToFloatArray(List<Vector2f> vectors){
        float[] vectorsStripped = new float[vectors.size() * 2];
        for (int i = 0; i < vectors.size(); i++) {
            vectorsStripped[i * 2] = vectors.get(i).x;
            vectorsStripped[i * 2 + 1] = vectors.get(i).y;
        }

        return vectorsStripped;
    }

    public static float[] v2ToFloatArray(Vector2f[] vectors){
        float[] vectorsStripped = new float[vectors.length * 2];
        for (int i = 0; i < vectors.length; i++) {
            vectorsStripped[i * 2] = vectors[i].x;
            vectorsStripped[i * 2 + 1] = vectors[i].y;
        }

        return vectorsStripped;
    }

    public static float[] v3ToFloatArray(List<Vector3f> vectors){
        float[] vectorsStripped = new float[vectors.size() * 3];
        int i = 0;
        for(Vector3f vector : vectors){
            vectorsStripped[i * 3] = vector.x;
            vectorsStripped[i * 3 + 1] = vector.y;
            vectorsStripped[i * 3 + 2] = vector.z;
            i++;
        }

        return vectorsStripped;
    }

    public static float[] v3ToFloatArray(Vector3f[] vectors){
        float[] vectorsStripped = new float[vectors.length * 3];
        for (int i = 0; i < vectors.length; i++) {
            vectorsStripped[i * 3] = vectors[i].x;
            vectorsStripped[i * 3 + 1] = vectors[i].y;
            vectorsStripped[i * 3 + 2] = vectors[i].z;
        }

        return vectorsStripped;
    }

    public static float angleTo360degrees(float angle) {
        // Convert radians to degrees
        angle = (float) Math.toDegrees(angle);
        // Normalize to [0, 360]
        angle = angle % 360;
        if (angle < 0) angle += 360;
        return angle;
    }

    public static float[] toFloatArray(List<Float> floatList){
        float[] floatArray = new float[floatList.size()];
        int i = 0;

        for (Float f : floatList) {
            floatArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
        }

        return floatArray;
    }

    public static String V3ToString(Vector3f vector){
        return "X " + FloatTwoDecimals(vector.x) + ", Y " + FloatTwoDecimals(vector.y) + ", Z " + FloatTwoDecimals(vector.z);
    }

    public static String V2ToString(Vector2f vector){
        return "X " + FloatTwoDecimals(vector.x) + ", Y " + FloatTwoDecimals(vector.y);
    }

    public static String FloatTwoDecimals(float number){
        boolean isNegative = number < 0;

        int intPart = (int) Math.abs(number);  // Get absolute value for the integer part
        int decimalPart = (int) ((Math.abs(number) - intPart) * 100);  // Get the decimal part (absolute value)

        // Build the string result
        return (isNegative ? "-" : "") + intPart + "." + (decimalPart < 10 ? "0" : "") + decimalPart;
    }

    public static Vector3i ToVector3i(Vector3f a){
        return new Vector3i((int)a.x, (int)a.y, (int)a.z);
    }

    public static Vector3f ToVector3f(Vector3i a){
        return new Vector3f(a.x, a.y, a.z);
    }

}
