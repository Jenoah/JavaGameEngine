package nl.jenoah.core.rendering;

import nl.jenoah.core.entity.Camera;
import nl.jenoah.core.components.RenderComponent;
import nl.jenoah.core.debugging.RenderMetrics;
import nl.jenoah.core.entity.GameObject;
import nl.jenoah.core.shaders.*;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import java.util.*;

public class ComponentRenderer implements IRenderer {

    Set<RenderComponent> renderObjects = new HashSet<>();
    HashMap<Shader, List<MeshMaterialSet>> sortedRenderObjects = new HashMap<>();
    HashMap<Shader, List<MeshMaterialSet>> sortedTransparentRenderObjects = new HashMap<>();

    private Matrix4f shadowSpaceMatrix = new Matrix4f();
    private int shadowMapID = 0;
    private Camera mainCamera;

    private RenderMetrics metrics;
    private boolean recordMetrics = false;

    @Override
    public void init() throws Exception {  }

    @Override
    public void render() {
        if (sortedRenderObjects.isEmpty() && sortedTransparentRenderObjects.isEmpty() || mainCamera == null) return;

        sortedRenderObjects.forEach((renderObjectShader, meshMaterialSetList) -> RenderPass(mainCamera, renderObjectShader, meshMaterialSetList));
        sortedTransparentRenderObjects.forEach((renderObjectShader, meshMaterialSetList) -> RenderPass(mainCamera, renderObjectShader, meshMaterialSetList));
    }

    private void RenderPass(Camera camera, Shader shader, List<MeshMaterialSet> meshMaterialSetList){
        if (recordMetrics) metrics.recordShaderBind();
        shader.bind();
        shader.render(camera);

        meshMaterialSetList.forEach(meshMaterialSet -> {
            if (!meshMaterialSet.getRoot().isEnabled() || !camera.isInFrustumAABB(meshMaterialSet.getRoot())) return;
            if (recordMetrics) metrics.recordStateChange();

            bind(meshMaterialSet);
            prepareShadow(meshMaterialSet);
            shader.prepare(meshMaterialSet, camera);

            if (recordMetrics) metrics.recordDrawCall();
            if(meshMaterialSet.mesh.isInstanced()){
                GL33.glDrawElementsInstanced(GL11.GL_TRIANGLES, meshMaterialSet.mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0, meshMaterialSet.mesh.getInstanceCount());
            }else{
                if(recordMetrics) metrics.recordVertexCount(meshMaterialSet.mesh.getVertexCount());
                GL11.glDrawElements(GL11.GL_TRIANGLES, meshMaterialSet.mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }

            unbind();
        });
        shader.unbind();
    }

    public void bind(MeshMaterialSet meshMaterialSet) {
        GL30.glBindVertexArray(meshMaterialSet.mesh.getVaoID());
        if (recordMetrics) metrics.recordVaoBind();

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
            if(meshMaterialSet.material.isTransparent()){
                if (!sortedTransparentRenderObjects.containsKey(meshMaterialSet.material.getShader())) {
                    List<MeshMaterialSet> meshMaterialSets = new ArrayList<>();
                    meshMaterialSets.add(meshMaterialSet);
                    sortedTransparentRenderObjects.put(meshMaterialSet.material.getShader(), meshMaterialSets);
                } else {
                    sortedTransparentRenderObjects.get(meshMaterialSet.material.getShader()).add(meshMaterialSet);
                }
            }else{
                if (!sortedRenderObjects.containsKey(meshMaterialSet.material.getShader())) {
                    List<MeshMaterialSet> meshMaterialSets = new ArrayList<>();
                    meshMaterialSets.add(meshMaterialSet);
                    sortedRenderObjects.put(meshMaterialSet.material.getShader(), meshMaterialSets);
                } else {
                    sortedRenderObjects.get(meshMaterialSet.material.getShader()).add(meshMaterialSet);
                }
            }
        });
    }

    public void dequeue(RenderComponent renderComponent) {
        //TODO: Make dequeue function for render component
    }

    public void setShadowSpaceMatrix(Matrix4f shadowSpaceMatrix){
        this.shadowSpaceMatrix = shadowSpaceMatrix;
    }

    public void setShadowMapID(int shadowMapID){
        this.shadowMapID = shadowMapID;
    }

    public void setMetrics(RenderMetrics metrics){
        this.metrics = metrics;
        recordMetrics = true;
    }

    public void recordMetrics(boolean recordMetrics) {
        this.recordMetrics = recordMetrics;
    }

    public void setMainCamera(Camera camera){
        this.mainCamera = camera;
    }
}
