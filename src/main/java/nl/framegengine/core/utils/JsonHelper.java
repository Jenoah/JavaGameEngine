package nl.framegengine.core.utils;

import nl.framegengine.core.IJsonSerializable;
import nl.framegengine.core.debugging.Debug;
import nl.framegengine.core.entity.GameObject;

import java.lang.reflect.*;
import java.util.*;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.json.*;

public class JsonHelper {
    public static boolean hasJsonKey(JsonObject o, String k) { return o.containsKey(k) && !o.isNull(k); }

    public static void loadVariableIntoObject(Object object, JsonObject objectInfo){
        loadVariableIntoObject(object, objectInfo, new String[]{});
    }

    public static void loadVariableIntoObject(Object object, JsonObject objectInfo, String[] keysToIgnore){
        for (String key : objectInfo.keySet()) {
            if (Arrays.asList(keysToIgnore).contains(key)) continue;

            JsonValue jsonValue = objectInfo.get(key);
            Field field = ClassHelper.findField(object.getClass(), key);
            if (field == null) continue;

            field.setAccessible(true);
            Class<?> fieldType = field.getType();

            try {
                if (jsonValue.getValueType() == JsonValue.ValueType.ARRAY) {
                    loadJsonArrayIntoObject(jsonValue, fieldType, field, object);
                } else if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
                    loadJsonObjectIntoObject(jsonValue, fieldType, field, object);
                } else {
                    if(fieldType.isAssignableFrom(GameObject.class) && jsonValue.getValueType() == JsonValue.ValueType.STRING){
                        field.set(object, GameObject.getByGUID(((JsonString)jsonValue).getString()));
                    }else {
                        Object value = JsonHelper.jsonToObject(jsonValue);
                        field.set(object, value);
                    }
                }
            } catch (Exception e) {
                Debug.LogError("Error loading variable '" + key + "': " + e.getMessage());
            }
        }
    }

    private static void loadJsonObjectIntoObject(JsonValue jsonValue, Class<?> fieldType, Field field, Object object) throws Exception {
        JsonObject nestedObj = jsonValue.asJsonObject();
        Object nestedInstance = field.get(object);

        if (nestedInstance == null) {
            if (nestedObj.containsKey("guid")) {
                String guid = nestedObj.getString("guid");
                GameObject existing = GameObject.getByGUID(guid);
                if (existing != null) {
                    field.set(object, existing);
                    nestedInstance = existing;
                } else {
                    Object newInstance = fieldType.getDeclaredConstructor().newInstance();
                    field.set(object, newInstance);
                    nestedInstance = newInstance;
                }
            } else {
                nestedInstance = fieldType.getDeclaredConstructor().newInstance();
                field.set(object, nestedInstance);
            }
        }

        if (nestedInstance instanceof IJsonSerializable serializable) {
            serializable.deserializeFromJson(nestedObj.toString());
        } else {
            loadVariableIntoObject(nestedInstance, nestedObj, new String[0]);
        }
    }

    private static void loadJsonArrayIntoObject(JsonValue jsonValue, Class<?> fieldType, Field field, Object object) throws Exception {
        JsonArray jsonArray = jsonValue.asJsonArray();

        if (fieldType.isArray()) {
            Class<?> componentType = fieldType.getComponentType();
            Object array = Array.newInstance(componentType, jsonArray.size());

            for (int i = 0; i < jsonArray.size(); i++) {
                Object element = convertJsonValueToObject(jsonArray.get(i), componentType);
                Array.set(array, i, element);
            }
            field.set(object, array);

        } else if (List.class.isAssignableFrom(fieldType)) {
            // For List fields
            Type genericType = field.getGenericType();
            Class<?> elementType = Object.class;
            if (genericType instanceof ParameterizedType) {
                Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
                elementType = (Class<?>) typeArgs[0];
            }
            List<Object> list = new ArrayList<>();
            for (JsonValue val : jsonArray) {
                list.add(convertJsonValueToObject(val, elementType));
            }
            field.set(object, list);
        }
    }

    private static Object convertJsonValueToObject(JsonValue jsonValue, Class<?> targetType) throws Exception {
        if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
            JsonObject jsonObj = jsonValue.asJsonObject();
            Object instance = targetType.getDeclaredConstructor().newInstance();
            if (instance instanceof IJsonSerializable serializable) {
                serializable.deserializeFromJson(jsonObj.toString());
                return instance;
            } else {
                loadVariableIntoObject(instance, jsonObj, new String[0]);
                return instance;
            }
        } else if (jsonValue.getValueType() == JsonValue.ValueType.ARRAY) {
            // Only needed for arrays-of-arrays, rare
            // Otherwise, handled at higher level
            return null;
        } else {
            return JsonHelper.jsonToObject(jsonValue); // your primitive/unboxing method
        }
    }

    /*
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

    /**/

    public static JsonObject objectToJson(Object object){
        return objectToJson(object, new String[]{});
    }

    public static JsonObject objectToJson(Object object, String[] valuesToIgnore){
        JsonObjectBuilder objectInfo = Json.createObjectBuilder();
        objectInfo.add("class", object.getClass().getName());
        if(object instanceof GameObject go && go.getParent() != null){
            objectInfo.add("parentGuid", go.getParent().getGuid());
        }

        Class<?> clazz = object.getClass();
        Object defaultInstance;
        try {
            defaultInstance = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create default instance for comparison: " + e.getMessage(), e);
        }

        List<Field> objectProperties = new ArrayList<>();
        ClassHelper.getAllProperties(objectProperties, object.getClass());

        Set<String> ignoreSet = new HashSet<>(Arrays.asList(valuesToIgnore));
        for (Field field : objectProperties) {
            if (ignoreSet.contains(field.getName())) continue;

            try {
                field.setAccessible(true);
                Object currentValue = field.get(object);
                Object defaultValue = field.get(defaultInstance);

                // Skip if current is null or equal to default
                if (currentValue == null && defaultValue == null) continue;
                if (currentValue != null && currentValue.equals(defaultValue)) continue;

                assert currentValue != null;
                addPropertyToJsonObject(objectInfo, field.getName(), currentValue);

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field '" + field.getName() + "': " + e.getMessage(), e);
            }
        }

        return objectInfo.build();
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
            case GameObject go -> jsonObjectBuilder.add(name, go.getGuid());
            case IJsonSerializable jsonSerializable -> jsonObjectBuilder.add(name, jsonSerializable.serializeToJson());
            case List<?> list -> {
                if(!list.isEmpty()){
                    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                    list.forEach(listItem -> addPropertyToJsonArray(jsonArrayBuilder, listItem));
                    JsonArray jsonArray = jsonArrayBuilder.build();
                    if(!jsonArray.isEmpty()) jsonObjectBuilder.add(name, jsonArray);
                }
            }
            case Set<?> set -> {
                if (!set.isEmpty()) {
                    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                    set.forEach(listItem -> addPropertyToJsonArray(jsonArrayBuilder, listItem));
                    JsonArray jsonArray = jsonArrayBuilder.build();
                    if(!jsonArray.isEmpty()) jsonObjectBuilder.add(name, jsonArray);
                }
            }
            default -> {}
        }
    }

    @SuppressWarnings("unchecked")
    public static void addPropertyToJsonArray(JsonArrayBuilder jsonArrayBuilder, Object object) {
        switch (object) {
            case Vector3f vector -> jsonArrayBuilder.add(JsonHelper.vector3ToJsonObject(vector));
            case Vector4f vector -> jsonArrayBuilder.add(JsonHelper.vector4ToJsonObject(vector));
            case Quaternionf quaternion -> jsonArrayBuilder.add(JsonHelper.quaternionToJsonObject(quaternion));
            case Float fl -> jsonArrayBuilder.add(fl);
            case Integer integer -> jsonArrayBuilder.add(integer);
            case Boolean bool -> jsonArrayBuilder.add(bool);
            case String str -> jsonArrayBuilder.add(str);
            case IJsonSerializable jsonSerializable -> jsonArrayBuilder.add(jsonSerializable.serializeToJson());
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
