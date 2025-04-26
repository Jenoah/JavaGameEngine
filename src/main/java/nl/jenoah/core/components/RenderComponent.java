package nl.jenoah.core.components;

import game.demo.DemoLauncher;
import nl.jenoah.core.entity.*;
import nl.jenoah.core.rendering.MeshMaterialSet;
import nl.jenoah.core.shaders.ShaderManager;

import java.util.ArrayList;
import java.util.List;

public class RenderComponent extends Component{

    private final List<MeshMaterialSet> meshMaterialSets = new ArrayList<>();

    public RenderComponent(Mesh mesh){
        addMesh(mesh);
    }

    public RenderComponent(Mesh mesh, Material material){
        addMesh(mesh, material);
    }

    public RenderComponent(MeshMaterialSet meshMaterialSet){
        addMesh(meshMaterialSet);
    }

    public RenderComponent(List<MeshMaterialSet> meshMaterialSets){
        addMeshes(meshMaterialSets);
    }

    public void addMesh(Mesh mesh){
        meshMaterialSets.add(new MeshMaterialSet(mesh, new Material(ShaderManager.pbrShader)).setRoot(this.getRoot()));
    }

    public void addMeshes(List<MeshMaterialSet> meshMaterialSets){
        List<MeshMaterialSet> localMeshMaterialSets = new ArrayList<>(meshMaterialSets);
        localMeshMaterialSets.forEach(meshMaterialSet -> meshMaterialSet.setRoot(this.getRoot()));
        this.meshMaterialSets.addAll(localMeshMaterialSets);
    }

    public void addMesh(Mesh mesh, Material material){
        meshMaterialSets.add(new MeshMaterialSet(mesh, material).setRoot(this.getRoot()));
    }

    public void addMesh(MeshMaterialSet meshMaterialSet){
        meshMaterialSets.add(meshMaterialSet.setRoot(this.getRoot()));
    }

    public List<MeshMaterialSet> getMeshMaterialSets(){
        return meshMaterialSets;
    }

    @Override
    public void initiate() {
        if(hasInitiated) return;
        super.initiate();
        queueRender();
    }

    @Override
    public Component setRoot(GameObject root){
        super.setRoot(root);

        meshMaterialSets.forEach((MeshMaterialSet) -> MeshMaterialSet.setRoot(root));
        return this;
    }

    private void queueRender(){
        DemoLauncher.getGame().getRenderer().queueRender(this);
    }

    private void dequeueRender(){
        DemoLauncher.getGame().getRenderer().dequeueRender(this);
    }
}
