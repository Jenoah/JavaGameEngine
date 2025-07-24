package nl.framegengine.core.rendering;

import nl.framegengine.core.IJsonSerializable;
import nl.framegengine.core.entity.GameObject;
import nl.framegengine.core.entity.Material;
import nl.framegengine.core.entity.Mesh;
import nl.framegengine.core.shaders.ShaderManager;
import nl.framegengine.core.utils.JsonHelper;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.StringReader;

public class MeshMaterialSet implements IJsonSerializable {
    public final Mesh mesh;
    public Material material;
    private GameObject root;

    public MeshMaterialSet(Mesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material;
    }

    public MeshMaterialSet(Mesh mesh) {
        this.mesh = mesh;
        this.material = new Material(ShaderManager.pbrShader);
    }

    public GameObject getRoot() {
        return this.root;
    }

    public MeshMaterialSet setRoot(GameObject root) {
        this.root = root;
        return this;
    }

    @Override
    public JsonObject serializeToJson() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        JsonObject jsonObject = JsonHelper.objectToJson(this, new String[]{"root"});
        jsonObject.forEach(jsonObjectBuilder::add);
        if(getRoot() != null) jsonObjectBuilder.add("root", root.getGuid());
        return jsonObjectBuilder.build();
    }

    @Override
    public void deserializeFromJson(String json) {
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonInfo = jsonReader.readObject();
        JsonHelper.loadVariableIntoObject(this, jsonInfo);
    }
}
