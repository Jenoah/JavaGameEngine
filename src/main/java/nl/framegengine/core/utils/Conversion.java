package nl.framegengine.core.utils;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.util.List;

public class Conversion {

    public static Vector3f[] floatArrayToVector3Array(float[] floatArray){
        Vector3f[] vectors = new Vector3f[floatArray.length / 3];
        for (int i = 0; i < vectors.length; i++) {
            Vector3f vector = new Vector3f();
            vector.x = floatArray[i * 3];
            vector.y = floatArray[i * 3 + 1];
            vector.z = floatArray[i * 3 + 2];
            vectors[i] = vector;
        }
        return vectors;
    }
    public static Vector2f[] floatArrayToVector2Array(float[] floatArray){
        Vector2f[] vectors = new Vector2f[floatArray.length / 2];
        for (int i = 0; i < vectors.length; i++) {
            Vector2f vector = new Vector2f();
            vector.x = floatArray[i * 2];
            vector.y = floatArray[i * 2 + 1];
            vectors[i] = vector;
        }
        return vectors;
    }

    public static float[] V2toFloatArray(List<Vector2f> vectors){
        float[] vectorsStripped = new float[vectors.size() * 2];
        for (int i = 0; i < vectors.size(); i++) {
            vectorsStripped[i * 2] = vectors.get(i).x;
            vectorsStripped[i * 2 + 1] = vectors.get(i).y;
        }

        return vectorsStripped;
    }
    public static float[] toFloatArray(Vector2f[] vectors){
        float[] vectorsStripped = new float[vectors.length * 2];
        for (int i = 0; i < vectors.length; i++) {
            vectorsStripped[i * 2] = vectors[i].x;
            vectorsStripped[i * 2 + 1] = vectors[i].y;
        }

        return vectorsStripped;
    }

    public static float[] V3toFloatArray(List<Vector3f> vectors){
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
    public static float[] toFloatArray(Vector3f[] vectors){
        float[] vectorsStripped = new float[vectors.length * 3];
        for (int i = 0; i < vectors.length; i++) {
            vectorsStripped[i * 3] = vectors[i].x;
            vectorsStripped[i * 3 + 1] = vectors[i].y;
            vectorsStripped[i * 3 + 2] = vectors[i].z;
        }

        return vectorsStripped;
    }

    public static int[] ToIntArray(List<Integer> integerArray){
        return integerArray.stream().mapToInt(i->i).toArray();
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

    public static float jsonToFloat(JsonObject obj, String key, float defaultValue) {
        if (!obj.containsKey(key) || obj.isNull(key)) return defaultValue;

        JsonValue value = obj.get(key);

        switch (value.getValueType()) {
            case NUMBER:
                return (float) ((JsonNumber) value).doubleValue();
            case STRING:
                String s = ((JsonString) value).getString().replace("f", "");
                try {
                    return Float.parseFloat(s);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            default:
                return defaultValue;
        }
    }

    public static Vector3f jsonToVector3f(JsonObject obj) {
        float x = jsonToFloat(obj, "x", 0.0f);
        float y = jsonToFloat(obj, "y", 0.0f);
        float z = jsonToFloat(obj, "z", 0.0f);
        return new Vector3f(x, y, z);
    }

    public static Quaternionf jsonToQuaternionf(JsonObject obj) {
        float x = jsonToFloat(obj, "x", 0.0f);
        float y = jsonToFloat(obj, "y", 0.0f);
        float z = jsonToFloat(obj, "z", 0.0f);
        float w = jsonToFloat(obj, "w", 1.0f);
        return new Quaternionf(x, y, z, w);
    }

    public static Object jsonToObject(JsonValue jsonValue) {
        switch (jsonValue.getValueType()) {
            case NUMBER:
                JsonNumber num = (JsonNumber) jsonValue;
                // Return as double or int depending on your needs
                if (num.isIntegral()) {
                    return num.intValue();
                } else {
                    return (float) num.doubleValue();
                }
            case STRING:
                return ((JsonString) jsonValue).getString();
            case TRUE:
                return true;
            case FALSE:
                return false;
            case NULL:
                return null;
            default:
                return null;
        }
    }
}
