package nl.framegengine.core.components;

import nl.framegengine.core.IJsonSerializable;
import nl.framegengine.core.entity.GameObject;
import nl.framegengine.core.entity.Material;
import nl.framegengine.core.entity.Mesh;
import nl.framegengine.core.loaders.OBJLoader.OBJLoader;
import nl.framegengine.core.rendering.MeshMaterialSet;
import nl.framegengine.core.rendering.RenderManager;
import nl.framegengine.core.shaders.ShaderManager;
import nl.framegengine.core.utils.AABB;
import nl.framegengine.core.utils.Constants;
import nl.framegengine.core.utils.JsonHelper;
import org.joml.Vector3f;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RenderComponent extends Component {

    protected List<String> meshPaths = new ArrayList<>();

    protected final Set<MeshMaterialSet> meshMaterialSets = new HashSet<>();

    public RenderComponent(){ }

    public RenderComponent(Mesh mesh) {
        addMesh(mesh);
    }

    public RenderComponent(Mesh mesh, Material material) {
        addMesh(mesh, material);
    }

    public RenderComponent(MeshMaterialSet meshMaterialSet) {
        addMesh(meshMaterialSet);
    }

    public RenderComponent(Set<MeshMaterialSet> meshMaterialSets) {
        addMeshes(meshMaterialSets);
    }

    public void addMesh(Mesh mesh) {
        meshMaterialSets.add(new MeshMaterialSet(mesh, new Material(ShaderManager.pbrShader)).setRoot(this.getRoot()));
        if(!meshPaths.contains(mesh.getMeshPath())) meshPaths.add(mesh.getMeshPath());
    }

    public void addMeshes(Set<MeshMaterialSet> meshMaterialSets) {
        Set<MeshMaterialSet> localMeshMaterialSets = new HashSet<>(meshMaterialSets);
        localMeshMaterialSets.forEach(meshMaterialSet -> {
            meshMaterialSet.setRoot(this.getRoot());
            if(!meshPaths.contains(meshMaterialSet.getMesh().getMeshPath())) meshPaths.add(meshMaterialSet.getMesh().getMeshPath());
        });
        this.meshMaterialSets.addAll(localMeshMaterialSets);
    }

    public void addMesh(Mesh mesh, Material material) {
        meshMaterialSets.add(new MeshMaterialSet(mesh, material).setRoot(this.getRoot()));
        if(!meshPaths.contains(mesh.getMeshPath())) meshPaths.add(mesh.getMeshPath());
    }

    public void addMesh(MeshMaterialSet meshMaterialSet) {
        meshMaterialSets.add(meshMaterialSet.setRoot(this.getRoot()));
        if(!meshPaths.contains(meshMaterialSet.getMesh().getMeshPath())) meshPaths.add(meshMaterialSet.getMesh().getMeshPath());
    }

    public Set<MeshMaterialSet> getMeshMaterialSets() {
        return meshMaterialSets;
    }

    @Override
    public void initiate() {
        if (hasInitiated) return;
        super.initiate();

        if(meshMaterialSets.isEmpty()){
            root.removeComponent(this);
            return;
        }
        calculateRadius();
        calculateAABB();
        queueRender();
    }

    @Override
    public Component setRoot(GameObject root) {
        super.setRoot(root);

        meshMaterialSets.forEach((MeshMaterialSet) -> MeshMaterialSet.setRoot(root));
        return this;
    }

    private void queueRender() {
        RenderManager.getInstance().queueRender(this);
    }

    private void dequeueRender() {
        RenderManager.getInstance().dequeueRender(this);
    }

    private void calculateRadius() {
        float maxRadius = 0;
        for (MeshMaterialSet meshMaterialSet : meshMaterialSets) {
            for (Vector3f vertex : meshMaterialSet.getMesh().getVertices()) {
                float distance = vertex.distance(Constants.VECTOR3_ZERO);
                if (distance > maxRadius) {
                    maxRadius = distance;
                }
            }
        }

        getRoot().setRadius(maxRadius);
    }

    public void calculateAABB() {
        Vector3f min = new Vector3f(Float.MAX_VALUE);
        Vector3f max = new Vector3f(-Float.MAX_VALUE);

        for (MeshMaterialSet meshMaterialSet : meshMaterialSets) {
            for (Vector3f vertex : meshMaterialSet.getMesh().getVertices()) {
                min.x = Math.min(min.x, vertex.x);
                min.y = Math.min(min.y, vertex.y);
                min.z = Math.min(min.z, vertex.z);
                max.x = Math.max(max.x, vertex.x);
                max.y = Math.max(max.y, vertex.y);
                max.z = Math.max(max.z, vertex.z);
            }
        }

        GameObject root = getRoot();
        if(root == null) return;

        if (min.length() == Float.MAX_VALUE || max.length() == -Float.MAX_VALUE) {
            root.setAabb(new AABB(new Vector3f(Constants.VECTOR3_ZERO), new Vector3f()));
            return;
        }

        getRoot().setCenter(new Vector3f(min).lerp(max, 0.5f));

        min.mul(root.getScale());
        max.mul(root.getScale());

        root.setAabb(new AABB(min, max));
    }

    @Override
    public void disable() {
        super.disable();
        dequeueRender();
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        meshMaterialSets.forEach(mms -> mms.getMesh().cleanUp());
    }

    @Override
    public JsonObject serializeToJson() {
        List<String> ignoredKeys = new ArrayList<>();
        ignoredKeys.add("hasInitiated");
        if(meshPaths.isEmpty()) ignoredKeys.add("meshPaths");

        return JsonHelper.objectToJson(this, ignoredKeys.toArray(new String[0]));
    }

    @Override
    public IJsonSerializable deserializeFromJson(String json) {
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jsonInfo = jsonReader.readObject();
        try {
            JsonHelper.loadVariableIntoObject(this, jsonInfo);
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        /* */

        if(!meshMaterialSets.isEmpty()){
            String meshPath = meshMaterialSets.stream().findFirst().get().getMesh().getMeshPath();
            Material mat = meshMaterialSets.stream().findFirst().get().material;
            meshMaterialSets.clear();
            if(meshPath.isEmpty() || meshPath.isBlank()){
                return this;
            }

            Set<MeshMaterialSet> mms = OBJLoader.loadOBJModel(meshPath);
            mms.forEach(meshMaterialSet -> {
                meshMaterialSet.setRoot(getRoot());
                meshMaterialSet.material = mat;
            });
            meshMaterialSets.addAll(mms);
        }

         /**/

        return this;
    }
}
