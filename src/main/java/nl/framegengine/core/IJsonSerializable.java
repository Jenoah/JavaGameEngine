package nl.framegengine.core;

import javax.json.JsonObject;

public interface IJsonSerializable {
    JsonObject serializeToJson();

    void deserializeFromJson(String json);
}
