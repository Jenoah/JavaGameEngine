package nl.jenoah.core.rendering;

import nl.jenoah.core.Camera;
import nl.jenoah.core.components.RenderComponent;
import nl.jenoah.core.debugging.RenderMetrics;
import nl.jenoah.core.entity.GameObject;
import nl.jenoah.core.shaders.*;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComponentRenderer implements IRenderer {

    List<RenderComponent> renderObjects = new ArrayList<>();
    HashMap<Shader, List<MeshMaterialSet>> sortedRenderObjects = new HashMap<>();
    private final RenderMetrics metrics = new RenderMetrics();
    private boolean recordMetrics = false;
    private Matrix4f shadowSpaceMatrix = new Matrix4f();
    private int shadowMapID = 0;

    @Override
    public void init() throws Exception {

    }

    @Override
    public void render(Camera camera) {
        if (recordMetrics) metrics.frameStart();
        if (sortedRenderObjects.isEmpty()) return;

        sortedRenderObjects.forEach((renderObjectShader, meshMaterialSetList) -> {
            if (recordMetrics) metrics.recordShaderBind();
            renderObjectShader.bind();
            renderObjectShader.render(camera);

            meshMaterialSetList.forEach(meshMaterialSet -> {
                if (!meshMaterialSet.getRoot().isEnabled()) return;
                if (recordMetrics) {
                    metrics.recordStateChange();
                    metrics.recordVaoBind();
                }
                bind(meshMaterialSet);
                prepareShadow(meshMaterialSet);
                renderObjectShader.prepare(meshMaterialSet, camera);


                if (recordMetrics) metrics.recordDrawCall();
                if(meshMaterialSet.mesh.isInstanced()){
                    GL33.glDrawElementsInstanced(GL11.GL_TRIANGLES, meshMaterialSet.mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0, meshMaterialSet.mesh.getInstanceCount());
                }else{
                    GL11.glDrawElements(GL11.GL_TRIANGLES, meshMaterialSet.mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                }

                unbind();


            });
            renderObjectShader.unbind();
        });

        if (recordMetrics) metrics.frameEnd();
    }

    public void bind(MeshMaterialSet meshMaterialSet) {
        GL30.glBindVertexArray(meshMaterialSet.mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if (meshMaterialSet.mesh.hasTangents()) {
            GL20.glEnableVertexAttribArray(3);
            GL20.glEnableVertexAttribArray(4);
        }
        if(meshMaterialSet.mesh.isInstanced()){
            GL20.glEnableVertexAttribArray(5);
            GL20.glEnableVertexAttribArray(6);
            GL20.glEnableVertexAttribArray(7);
            GL20.glEnableVertexAttribArray(8);
        }
        if (meshMaterialSet.material.isDoubleSided()) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_CULL_FACE);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }

    private void prepareShadow(MeshMaterialSet meshMaterialSet){
        if(meshMaterialSet.material.receiveShadows() && meshMaterialSet.material.getShader() instanceof SimpleLitShader) {
                ((SimpleLitShader) meshMaterialSet.material.getShader()).setShadowSpaceMatrix(shadowSpaceMatrix);
            GL13.glActiveTexture(GL13.GL_TEXTURE9);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapID);
            meshMaterialSet.material.getShader().setTexture("shadowMap", 9);
        }
    }

    @Override
    public void unbind() {
        for (int i = 0; i < 8; i++) {
            GL20.glDisableVertexAttribArray(i);
        }
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(GameObject entity, Camera camera) {

    }

    @Override
    public void cleanUp() {

    }

    public void queue(RenderComponent renderComponent) {
        this.renderObjects.add(renderComponent);
        renderComponent.getMeshMaterialSets().forEach(meshMaterialSet -> {
            if (!sortedRenderObjects.containsKey(meshMaterialSet.material.getShader())) {
                List<MeshMaterialSet> meshMaterialSets = new ArrayList<>();
                meshMaterialSets.add(meshMaterialSet);
                sortedRenderObjects.put(meshMaterialSet.material.getShader(), meshMaterialSets);
            } else {
                sortedRenderObjects.get(meshMaterialSet.material.getShader()).add(meshMaterialSet);
            }
        });
    }

    public void dequeue(RenderComponent renderComponent) {
        //TODO: Make dequeue function for render component
    }

    public void recordMetrics(boolean recordMetrics) {
        this.recordMetrics = recordMetrics;
    }

    public String getMetrics() {
        return recordMetrics ? metrics.getMetrics() : "Metrics not recorded";
    }

    public void setShadowSpaceMatrix(Matrix4f shadowSpaceMatrix){
        this.shadowSpaceMatrix = shadowSpaceMatrix;
    }

    public void setShadowMapID(int shadowMapID){
        this.shadowMapID = shadowMapID;
    }
}
