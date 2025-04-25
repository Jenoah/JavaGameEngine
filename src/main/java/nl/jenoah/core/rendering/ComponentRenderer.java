package nl.jenoah.core.rendering;

import nl.jenoah.core.Camera;
import nl.jenoah.core.components.RenderComponent;
import nl.jenoah.core.entity.GameObject;
import nl.jenoah.core.shaders.Shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComponentRenderer implements IRenderer{

    List<RenderComponent> renderObjects = new ArrayList<>();
    HashMap<Shader, List<MeshMaterialSet>> sortedRenderObjects = new HashMap<>();

    @Override
    public void init() throws Exception {

    }

    @Override
    public void render(Camera camera) {
        if(sortedRenderObjects.isEmpty()) return;

        sortedRenderObjects.forEach((renderObjectShader, meshMaterialSetList) -> {
            renderObjectShader.bind();
            renderObjectShader.render(camera);
            meshMaterialSetList.forEach(meshMaterialSet -> {
                bind(meshMaterialSet);
                renderObjectShader.prepare(meshMaterialSet, camera);

                GL11.glDrawElements(GL11.GL_TRIANGLES, meshMaterialSet.mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

                unbind();


            });
            renderObjectShader.unbind();
        });
    }

    public void bind(MeshMaterialSet meshMaterialSet) {
        GL30.glBindVertexArray(meshMaterialSet.mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if(meshMaterialSet.mesh.hasTangents()){
            GL20.glEnableVertexAttribArray(3);
            GL20.glEnableVertexAttribArray(4);
        }
        if(meshMaterialSet.material.isDoubleSided()){
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_CULL_FACE);
        }else{
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }

    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(GameObject entity, Camera camera) {

    }

    @Override
    public void cleanUp() {

    }

    public void queue(RenderComponent renderComponent){
        this.renderObjects.add(renderComponent);
        renderComponent.getMeshMaterialSets().forEach(meshMaterialSet -> {
            if(!sortedRenderObjects.containsKey(meshMaterialSet.material.getShader())){
                List<MeshMaterialSet> meshMaterialSets = new ArrayList<>();
                meshMaterialSets.add(meshMaterialSet);
                sortedRenderObjects.put(meshMaterialSet.material.getShader(), meshMaterialSets);
            }else{
                sortedRenderObjects.get(meshMaterialSet.material.getShader()).add(meshMaterialSet);
            }
        });
    }
}
