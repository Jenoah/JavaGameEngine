package nl.jenoah.core.components;

import game.demo.DemoLauncher;
import nl.jenoah.core.entity.*;
import nl.jenoah.core.rendering.MeshMaterialSet;
import nl.jenoah.core.shaders.ShaderManager;
import nl.jenoah.core.utils.AABB;
import nl.jenoah.core.utils.Constants;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class RenderComponent extends Component {

    private final Set<MeshMaterialSet> meshMaterialSets = new HashSet<>();

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
    }

    public void addMeshes(Set<MeshMaterialSet> meshMaterialSets) {
        Set<MeshMaterialSet> localMeshMaterialSets = new HashSet<>(meshMaterialSets);
        localMeshMaterialSets.forEach(meshMaterialSet -> meshMaterialSet.setRoot(this.getRoot()));
        this.meshMaterialSets.addAll(localMeshMaterialSets);
    }

    public void addMesh(Mesh mesh, Material material) {
        meshMaterialSets.add(new MeshMaterialSet(mesh, material).setRoot(this.getRoot()));
    }

    public void addMesh(MeshMaterialSet meshMaterialSet) {
        meshMaterialSets.add(meshMaterialSet.setRoot(this.getRoot()));
    }

    public Set<MeshMaterialSet> getMeshMaterialSets() {
        return meshMaterialSets;
    }

    @Override
    public void initiate() {
        if (hasInitiated) return;
        super.initiate();
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
        DemoLauncher.getGame().getRenderer().queueRender(this);
    }

    private void dequeueRender() {
        DemoLauncher.getGame().getRenderer().dequeueRender(this);
    }

    private void calculateRadius() {
        float maxRadius = 0;
        for (MeshMaterialSet meshMaterialSet : meshMaterialSets) {
            for (Vector3f vertex : meshMaterialSet.mesh.getVertices()) {
                float distance = vertex.distance(Constants.VECTOR3_ZERO);
                if (distance > maxRadius) {
                    maxRadius = distance;
                }
            }
        }

        getRoot().setRadius(maxRadius);
    }

    private void calculateAABB() {
        Vector3f min = new Vector3f(Float.MAX_VALUE);
        Vector3f max = new Vector3f(-Float.MAX_VALUE);

        for (MeshMaterialSet meshMaterialSet : meshMaterialSets) {
            for (Vector3f vertex : meshMaterialSet.mesh.getVertices()) {
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
}
