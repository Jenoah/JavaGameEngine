package nl.framegengine.core.rendering;

import nl.framegengine.core.entity.GameObject;
import nl.framegengine.core.entity.Material;
import nl.framegengine.core.entity.Mesh;
import nl.framegengine.core.shaders.ShaderManager;

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
