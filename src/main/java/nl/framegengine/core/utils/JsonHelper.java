package nl.framegengine.core.utils;

import nl.framegengine.core.IJsonSerializable;
import nl.framegengine.core.components.Component;
import nl.framegengine.core.debugging.Debug;
import nl.framegengine.core.entity.GameObject;

import java.lang.reflect.*;
import java.util.*;

import nl.framegengine.core.entity.SceneManager;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import javax.json.*;

public class JsonHelper {
    public static boolean hasJsonKey(JsonObject o, String k) { return o.containsKey(k) && !o.isNull(k); }

    public static void loadVariableIntoObject(Object object, JsonValue objectInfo) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        loadVariableIntoObject(object, objectInfo, new String[]{});
    }

    /*
    public static void loadVariableIntoObject(Object targetObj, JsonValue jsonValue, String[] keysToIgnore) throws Exception {
        if (jsonValue == null || targetObj == null) return;

        if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
            JsonObject jsonObject = jsonValue.asJsonObject();

            for (String key : jsonObject.keySet()) {
                if (Arrays.asList(keysToIgnore).contains(key) || key.equals("class")) continue;

                JsonValue fieldJsonValue = jsonObject.get(key);

                Field field = ClassHelper.findField(targetObj.getClass(), key);
                if (field == null) continue; // Skip unknown or computed fields

                field.setAccessible(true); // Already set, but safe

                Class<?> fieldType = field.getType();

                if (fieldJsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject fieldJsonObject = fieldJsonValue.asJsonObject();

                    // Handle Vector3f, Vector4f, Color, etc.
                    if (ClassHelper.isValueObject(fieldType)) {
                        for (String nestedKey : fieldJsonObject.keySet()) {
                            Object nestedValue = JsonHelper.jsonToObject(fieldJsonObject.get(nestedKey), fieldType);
                            ClassHelper.setDeepProperty(targetObj, key + "." + nestedKey, nestedValue);
                        }
                        continue;
                    }

                    // For user-defined IJsonSerializable or polymorphic type
                    String implClassName = fieldType.getName();
                    if (JsonHelper.hasJsonKey(fieldJsonObject, "class")) {
                        implClassName = fieldJsonObject.getString("class");
                    }

                    Object fieldInstance;
                    if(fieldType.isAssignableFrom(Component.class)) {
                        fieldInstance = SceneManager.componentLoader.loadComponent(implClassName);
                    }else if(fieldType.isAssignableFrom(GameObject.class) && jsonValue.getValueType() == JsonValue.ValueType.STRING){
                        fieldInstance = GameObject.getByGUID(((JsonString)jsonValue).getString());
                    }else {
                        fieldInstance = Class.forName(implClassName).getDeclaredConstructor().newInstance();
                    }

                    if (fieldInstance instanceof IJsonSerializable serializable) {
                            fieldInstance = serializable.deserializeFromJson(fieldJsonObject.toString());
                    } else {
                        loadVariableIntoObject(fieldInstance, fieldJsonObject, new String[0]);
                    }
                    field.set(targetObj, fieldInstance);

                } else if (fieldJsonValue.getValueType() == JsonValue.ValueType.ARRAY) {
                    // For List<T> fields
                    Collection<Object> collection;
                    if (Set.class.isAssignableFrom(fieldType)) {
                        collection = new HashSet<>();
                    } else if (List.class.isAssignableFrom(fieldType)) {
                        collection = new ArrayList<>();
                    } else if (!fieldType.isInterface()) {
                        collection = (Collection<Object>) fieldType.getDeclaredConstructor().newInstance();
                    } else {
                        throw new RuntimeException("Cannot instantiate collection type: " + fieldType.getName());
                    }

                    Class<?> elementType = ClassHelper.getFieldGenericType(field);

                    for (JsonValue elemJson : fieldJsonValue.asJsonArray()) {
                        Object elemObj;
                        if (elemJson.getValueType() == JsonValue.ValueType.OBJECT) {
                            JsonObject elemObjJson = elemJson.asJsonObject();
                            String elemClassName = elementType.getName();
                            if (JsonHelper.hasJsonKey(elemObjJson, "class")) {
                                elemClassName = elemObjJson.getString("class");
                            }
                            if(elementType.isAssignableFrom(Component.class)){
                                elemObj = SceneManager.componentLoader.loadComponent(elemClassName);
                                ((Component)elemObj).setRoot((GameObject)targetObj);
                            }else {
                                elemObj = Class.forName(elemClassName).getDeclaredConstructor().newInstance();
                            }
                            if (elemObj instanceof IJsonSerializable serializable) {
                                elemObj = serializable.deserializeFromJson(elemObjJson.toString());
                            } else {
                                loadVariableIntoObject(elemObj, elemObjJson, new String[0]);
                            }
                        } else {
                            elemObj = JsonHelper.jsonToObject(elemJson, elementType);
                        }
                        collection.add(elemObj);
                    }
                    field.set(targetObj, collection);

                } else {
                    // Primitive, string, enum etc.
                    Object value = null;
                    if(fieldType.isAssignableFrom(GameObject.class) && fieldJsonValue.getValueType() == JsonValue.ValueType.STRING){
                        value = GameObject.getByGUID(((JsonString) fieldJsonValue).getString());
                    } else {
                        value = JsonHelper.jsonToObject(fieldJsonValue, fieldType);
                    }
                    field.set(targetObj, value);
                }
            }
        }
    }
    /**/

    public static void loadVariableIntoObject(Object object, JsonValue jsonValue, String[] keysToIgnore) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        switch (jsonValue.getValueType()) {
            case OBJECT:
                JsonObject jsonObject = jsonValue.asJsonObject();
                for (String key : jsonObject.keySet()) {
                    if (Arrays.asList(keysToIgnore).contains(key)) continue;
                    JsonValue jsonItemValue = jsonObject.get(key);

                    Field field = ClassHelper.findField(object.getClass(), key);
                    if(field == null) continue;
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    String fieldTypeName = fieldType.getName();

                    if (jsonItemValue.getValueType() == JsonValue.ValueType.OBJECT) {
                        JsonObject jsonObjectItem = jsonItemValue.asJsonObject();

                        if (JsonHelper.hasJsonKey(jsonObjectItem, "class")) {
                            fieldTypeName = jsonObjectItem.getString("class");
                        }

                        Object fieldValue;

                        if (ClassHelper.isValueObject(fieldType)) {
                            fieldValue = jsonToCustomProperty(jsonObjectItem, fieldType);
                        } else {
                            fieldValue = field.get(object);
                            if (fieldValue == null) {
                                fieldValue = Class.forName(fieldTypeName).getDeclaredConstructor().newInstance();
                            }
                            loadVariableIntoObject(fieldValue, jsonItemValue);
                        }

                        if(fieldValue instanceof IJsonSerializable) ((IJsonSerializable)fieldValue).deserializeFromJson(jsonItemValue.toString());
                        field.set(object, fieldValue);

                    }else if(jsonItemValue.getValueType() == JsonValue.ValueType.ARRAY) {
                        Collection<Object> collection = ClassHelper.createCollectionOfType(fieldType);

                        Class<?> arrayItemType = ClassHelper.getFieldGenericType(field);

                        for (JsonValue arrayItemJsonValue : jsonItemValue.asJsonArray()) {
                            Object arrayItemInstantiatedObject;

                            if (arrayItemJsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
                                JsonObject arrayItemJsonObject = arrayItemJsonValue.asJsonObject();

                                fieldTypeName = arrayItemType.getName();
                                if (JsonHelper.hasJsonKey(arrayItemJsonObject, "class")) {
                                    fieldTypeName = arrayItemJsonObject.getString("class");
                                }

                                Debug.Log("Field is a " + arrayItemType.getSimpleName() + " vs " + fieldTypeName);

                                if(arrayItemType.isAssignableFrom(Component.class)){
                                    arrayItemInstantiatedObject = SceneManager.componentLoader.loadComponent(fieldTypeName);
                                    ((Component)arrayItemInstantiatedObject).setRoot((GameObject) object);
                                }else {
                                    arrayItemInstantiatedObject = Class.forName(fieldTypeName).getDeclaredConstructor().newInstance();
                                }

                                if (arrayItemInstantiatedObject instanceof IJsonSerializable serializable) {
                                    Debug.Log("Deserializing " + serializable.getClass().getSimpleName());
                                    arrayItemInstantiatedObject = serializable.deserializeFromJson(arrayItemJsonObject.toString());
                                }else{
                                    loadVariableIntoObject(arrayItemInstantiatedObject, arrayItemJsonObject, new String[0]);
                                }
                            } else {
                                arrayItemInstantiatedObject = JsonHelper.jsonToObject(arrayItemJsonValue);
                            }

                            collection.add(arrayItemInstantiatedObject);
                        }
                        Debug.Log("The list is filled with " + collection.size() + " items");

                        field.set(object, collection);

                    }else{ //IF IS INSTANCE OF AN OBJECT
                        Object fieldValue = null;
                        if(fieldType.isAssignableFrom(GameObject.class) && jsonItemValue.getValueType() == JsonValue.ValueType.STRING){
                            fieldValue = GameObject.getByGUID(((JsonString)jsonItemValue).getString());
                        }else {
                            fieldValue = jsonToObject(jsonItemValue);
                        }

                        //TODO: COMPONENT ROOT WILL PROBABLY BE OVERWRITTEN HERE
                        if (fieldValue != null && fieldValue.getClass().isAssignableFrom(IJsonSerializable.class)) {
                            ((IJsonSerializable) fieldValue).deserializeFromJson(((JsonString) jsonItemValue).getString());
                        }
                        field.set(object, fieldValue);
                    }
                }
                break;
            case ARRAY:
                JsonArray jsonArray = jsonValue.asJsonArray();
                Debug.Log("Array is " + jsonArray.toString());
                jsonArray.forEach(jsonArrayItem -> {

                });
                break;
            case null, default:
                break;
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

    public static Object jsonToCustomProperty(JsonObject jsonObject, Class<?> type) {
        if(type.isAssignableFrom(Vector3f.class)) return jsonToVector3f(jsonObject);
        if(type.isAssignableFrom(Vector4f.class)) return jsonToVector4f(jsonObject);
        if(type.isAssignableFrom(Quaternionf.class)) return jsonToQuaternionf(jsonObject);
        return null;
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

    public static Vector4f jsonToVector4f(JsonObject obj) {
        float x = jsonToFloat(obj, "x", 0.0f);
        float y = jsonToFloat(obj, "y", 0.0f);
        float z = jsonToFloat(obj, "z", 0.0f);
        float w = jsonToFloat(obj, "w", 0.0f);
        return new Vector4f(x, y, z, w);
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
            case STRING: return ((JsonString) jsonValue).getString();
            case TRUE: return true;
            case FALSE: return false;
            default: return null;
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
