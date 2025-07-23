package nl.framegengine.core.utils;

import nl.framegengine.core.components.Component;
import nl.framegengine.core.entity.GameObject;
import java.util.Set;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.json.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonHelper {
    public static boolean hasJsonKey(JsonObject o, String k) { return o.containsKey(k) && !o.isNull(k); }

    public static void loadVariableIntoObject(Object object, JsonObject objectInfo){
        loadVariableIntoObject(object, objectInfo, new String[]{});
    }

    public static void loadVariableIntoObject(Object object, JsonObject objectInfo, String[] keysToIgnore){
        for (String key : objectInfo.keySet()) {
            if (Arrays.asList(keysToIgnore).contains(key)) continue;

            JsonValue jsonValue = objectInfo.get(key);
            if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
                JsonObject nestedObj = jsonValue.asJsonObject();
                for (String nestedKey : nestedObj.keySet()) {
                    Object nestedValue = JsonHelper.jsonToObject(nestedObj.get(nestedKey));
                    ClassHelper.setDeepProperty(object, key + "." + nestedKey, nestedValue);
                }
                // Nested object, set each nested field using deep property
            } else if (jsonValue.getValueType() != JsonValue.ValueType.ARRAY) {
                Object value = JsonHelper.jsonToObject(jsonValue);
                ClassHelper.setProperty(object, key, value);
            }
        }
    }

    public static JsonObject objectToJson(Object object){
        return objectToJson(object, new String[]{});
    }

    public static JsonObject objectToJson(Object object, String[] valuesToIgnore){
        JsonObjectBuilder objectInfo = Json.createObjectBuilder();
        objectInfo.add("class", object.getClass().getSimpleName());
        if(object instanceof GameObject go && go.getParent() != null){
            objectInfo.add("parentGuid", go.getParent().getGuid());
        }

        List<Field> objectProperties = new ArrayList<>();
        ClassHelper.getAllProperties(objectProperties, object.getClass());

        //TODO: Filter out variables that are the same as the default value
        objectProperties.forEach(field -> {
            Object value = null;
            try {
                field.setAccessible(true);
                value = field.get(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if(value == null || Arrays.stream(valuesToIgnore).toList().contains(field.getName())) return;
            addPropertyToJsonObject(objectInfo, field.getName(), value);
        });

        return objectInfo.build();
    }

    public static JsonArray componentsToJsonArray(Set<Component> components){
        JsonArrayBuilder componentArray = Json.createArrayBuilder();
        components.forEach(component -> {
            if(component.getClass().getSimpleName().equals("RenderComponent")) return;
            JsonObject componentInfo = objectToJson(component, new String[]{"hasInitiated"});
            componentArray.add(componentInfo);
        });
        return componentArray.build();
    }

    @SuppressWarnings("unchecked")
    public static void addPropertyToJsonObject(JsonObjectBuilder jsonObjectBuilder, String name, Object object) {
        switch (object) {
            case Vector3f vector -> jsonObjectBuilder.add(name, JsonHelper.vector3ToJsonObject(vector));
            case Vector4f vector -> jsonObjectBuilder.add(name, JsonHelper.vector4ToJsonObject(vector));
            case Quaternionf quaternion -> jsonObjectBuilder.add(name, JsonHelper.quaternionToJsonObject(quaternion));
            case Float fl -> jsonObjectBuilder.add(name, fl);
            case Integer integer -> jsonObjectBuilder.add(name, integer);
            case Boolean bool -> jsonObjectBuilder.add(name, bool);
            case String str -> jsonObjectBuilder.add(name, str);
            case Set<?> set -> {
                if(!set.isEmpty() && set.stream().findAny().get() instanceof Component){
                    JsonArray componentsArray = componentsToJsonArray((Set<Component>) set);
                    if(!componentsArray.isEmpty()) jsonObjectBuilder.add("components", componentsArray);
                }
            }
            default -> {}
        }
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

    public static JsonArray vector3ToJsonArray(Vector3f vector){
        JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        jsonArray.add(vector.x);
        jsonArray.add(vector.y);
        jsonArray.add(vector.z);

        return jsonArray.build();
    }

    public static JsonArray vector4ToJsonArray(Vector4f vector){
        JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        jsonArray.add(vector.x);
        jsonArray.add(vector.y);
        jsonArray.add(vector.z);
        jsonArray.add(vector.w);

        return jsonArray.build();
    }

    public static JsonArray quaternionToJsonArray(Quaternionf quaternion){
        JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        jsonArray.add(quaternion.x);
        jsonArray.add(quaternion.y);
        jsonArray.add(quaternion.z);
        jsonArray.add(quaternion.w);

        return jsonArray.build();
    }

    public static JsonObject vector3ToJsonObject(Vector3f vector){
        JsonObjectBuilder jsonObject = Json.createObjectBuilder();
        jsonObject.add("x", vector.x);
        jsonObject.add("y", vector.y);
        jsonObject.add("z", vector.z);

        return jsonObject.build();
    }

    public static JsonObject vector4ToJsonObject(Vector4f vector){
        JsonObjectBuilder jsonObject = Json.createObjectBuilder();
        jsonObject.add("x", vector.x);
        jsonObject.add("y", vector.y);
        jsonObject.add("z", vector.z);
        jsonObject.add("w", vector.w);

        return jsonObject.build();
    }

    public static JsonObject quaternionToJsonObject(Quaternionf quaternion){
        JsonObjectBuilder jsonObject = Json.createObjectBuilder();
        jsonObject.add("x", quaternion.x);
        jsonObject.add("y", quaternion.y);
        jsonObject.add("z", quaternion.z);
        jsonObject.add("w", quaternion.w);

        return jsonObject.build();
    }
}
