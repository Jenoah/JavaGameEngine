package nl.jenoah.core.rendering;

import nl.jenoah.core.entity.GameObject;
import nl.jenoah.core.entity.Material;
import nl.jenoah.core.entity.Mesh;
import nl.jenoah.core.shaders.ShaderManager;

public class MeshMaterialSet {
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
}
