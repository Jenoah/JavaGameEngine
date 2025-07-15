package nl.jenoah.core.utils;

import nl.jenoah.core.debugging.Debug;
import org.lwjgl.system.MemoryUtil;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Utils {
    public static SimplexNoise simplexNoise = new SimplexNoise();
    public static FastNoise fastNoise = new FastNoise();

    public static FloatBuffer storeDataInFloatBuffer(float[] data){
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static void setNoiseSeed(int seed){
        SimplexNoise.RANDOMSEED = seed;
        simplexNoise = new SimplexNoise();
        fastNoise.SetSeed(seed);
    }

    public static IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static String loadResource(String fileName) throws Exception{
        String result;
        try(InputStream in = Utils.class.getResourceAsStream(fileName);
        Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)){
            result = scanner.useDelimiter("\\A").next();
        }

        return result;
    }

    public static List<String> readAllLines(String fileName) {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String getFileName(String filePath) {
        String fileName = new File(filePath).getName();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public static List<File> findAllJavaFiles(File rootDir) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = rootDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    javaFiles.addAll(findAllJavaFiles(file));
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
        return javaFiles;
    }

    public static boolean hasJsonKey(JsonObject o, String k) { return o.containsKey(k) && !o.isNull(k); }

    public static void setProperty(Object instance, String fieldName, Object value) {
        Class<?> clazz = instance.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(instance, value);
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
                Field field = Utils.findField(currentObj.getClass(), fields[i]);
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
            Field lastField = Utils.findField(currentObj.getClass(), fields[fields.length - 1]);
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
                    Object nestedValue = Conversion.jsonToObject(nestedObj.get(nestedKey));
                    Utils.setDeepProperty(object, key + "." + nestedKey, nestedValue);
                }
                // Nested object, set each nested field using deep property
            } else if (jsonValue.getValueType() != JsonValue.ValueType.ARRAY) {
                Object value = Conversion.jsonToObject(jsonValue);
                Utils.setProperty(object, key, value);
            }
        }
    }
}
