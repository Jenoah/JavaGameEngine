package nl.jenoah.core.utils;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.Arrays;

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
                    Object nestedValue = Conversion.jsonToObject(nestedObj.get(nestedKey));
                    ClassHelper.setDeepProperty(object, key + "." + nestedKey, nestedValue);
                }
                // Nested object, set each nested field using deep property
            } else if (jsonValue.getValueType() != JsonValue.ValueType.ARRAY) {
                Object value = Conversion.jsonToObject(jsonValue);
                ClassHelper.setProperty(object, key, value);
            }
        }
    }
}
