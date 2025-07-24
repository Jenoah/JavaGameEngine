package nl.framegengine.core.utils;

import nl.framegengine.core.debugging.Debug;
import nl.framegengine.core.entity.GameObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ClassHelper {
    public static void setProperty(Object instance, String fieldName, Object value) {
        Class<?> clazz = instance.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                if(field.getType() == GameObject.class && value instanceof String guid){
                    GameObject gameObject = GameObject.getByGUID(guid);
                    if(gameObject != null) field.set(instance, GameObject.getByGUID(guid));
                }else {
                    field.set(instance, value);
                }
                return; // success, exit method
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass(); // try superclass
            } catch (IllegalAccessException e) {
                Debug.LogError("Access denied to field: " + fieldName);
                return;
            }
        }
        Debug.LogError("Field '" + fieldName + "' not found in class " + instance.getClass().getName());
    }

    public static void setDeepProperty(Object instance, String propertyPath, Object value) {
        String[] fields = propertyPath.split("\\.");
        Object currentObj = instance;
        try {
            for (int i = 0; i < fields.length - 1; i++) {
                Field field = ClassHelper.findField(currentObj.getClass(), fields[i]);
                if (field == null) {
                    Debug.LogError("Field '" + fields[i] + "' not found while traversing " + propertyPath);
                    return;
                }
                field.setAccessible(true);
                Object nestedObj = field.get(currentObj);
                if (nestedObj == null) {
                    nestedObj = field.getType().getDeclaredConstructor().newInstance();
                    field.set(currentObj, nestedObj);
                }
                currentObj = nestedObj;
            }
            Field lastField = ClassHelper.findField(currentObj.getClass(), fields[fields.length - 1]);
            if (lastField == null) {
                Debug.LogError("Field '" + fields[fields.length - 1] + "' not found in " + currentObj.getClass().getName());
                return;
            }
            lastField.setAccessible(true);
            lastField.set(currentObj, value);
        } catch (Exception e) {
            Debug.LogError("Failed to set property '" + propertyPath + "': " + e.getMessage());
        }
    }

    public static void getAllProperties(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllProperties(fields, type.getSuperclass());
        }
    }

    public static void getAllPublicAndProtectedProperties(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.stream(type.getDeclaredFields()).filter(f -> Modifier.isPublic(f.getModifiers())  || Modifier.isProtected(f.getModifiers())).toList());

        if (type.getSuperclass() != null) {
            getAllPublicAndProtectedProperties(fields, type.getSuperclass());
        }
    }

    public static Field getFieldFromObject(String fieldName, Class<?> type) throws NoSuchFieldException {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = type.getSuperclass();
            if (superClass != null) {
                return getFieldFromObject(fieldName, superClass);
            } else {
                throw e;
            }
        }
    }

    public static void getAllPublicProperties(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.stream(type.getDeclaredFields()).filter(f -> Modifier.isPublic(f.getModifiers())).toList());

        if (type.getSuperclass() != null) {
            getAllPublicProperties(fields, type.getSuperclass());
        }
    }

    public static Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
