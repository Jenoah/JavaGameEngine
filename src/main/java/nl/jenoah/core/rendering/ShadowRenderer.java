package nl.jenoah.core.rendering;

import nl.jenoah.core.Camera;
import nl.jenoah.core.components.RenderComponent;
import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.debugging.RenderMetrics;
import nl.jenoah.core.entity.GameObject;
import nl.jenoah.core.entity.Scene;
import nl.jenoah.core.shaders.ShadowShader;
import nl.jenoah.core.utils.Constants;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import java.util.ArrayList;
import java.util.List;


public class ShadowRenderer implements IRenderer{
    private final List<MeshMaterialSet> shadowSets = new ArrayList<>();
    private ShadowShader shadowShader;
    private ShadowFrameBuffer shadowFrameBuffer;
    private ShadowFrustum shadowFrustum;
    private RenderMetrics metrics;
    private boolean recordMetrics = false;

    private Matrix4f lightViewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f projectionViewMatrix = new Matrix4f();
    private Matrix4f offset = createOffset();

    @Override
    public void init() throws Exception {
        shadowFrameBuffer = new ShadowFrameBuffer(Constants.SHADOW_MAP_SIZE, Constants.SHADOW_MAP_SIZE);
        shadowShader = new ShadowShader();
        shadowShader.init();
        shadowFrustum = new ShadowFrustum();
    }

    @Override
    public void render(Camera camera) {
        Debug.LogError("Using wrong render method for rendering shadows");
    }

    public void render(Scene currentScene) {
        if(shadowSets.isEmpty()) return;

        shadowFrustum.setCamera(currentScene.getPlayer().getCamera());

        prepare(currentScene.getDirectionalLight().getForward(), shadowFrustum);

        if (recordMetrics) metrics.recordShaderBind();
        shadowShader.bind();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);

        shadowSets.forEach((meshMaterialSet) -> {
            if (!meshMaterialSet.getRoot().isEnabled()) return;
            if (recordMetrics) {
                metrics.recordStateChange();
                metrics.recordVaoBind();
            }

            bind(meshMaterialSet);

            shadowShader.prepare(meshMaterialSet, projectionViewMatrix);

            if (recordMetrics) metrics.recordDrawCall();
            if(meshMaterialSet.mesh.isInstanced()){
                GL33.glDrawElementsInstanced(GL11.GL_TRIANGLES, meshMaterialSet.mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0, meshMaterialSet.mesh.getInstanceCount());
            }else{
                GL11.glDrawElements(GL11.GL_TRIANGLES, meshMaterialSet.mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }

            unbind();
        });

        shadowFrameBuffer.unbindFrameBuffer();
        GL11.glCullFace(GL11.GL_BACK);
        shadowShader.unbind();
    }

    @Override
    public void bind(MeshMaterialSet meshMaterialSet) {
        GL30.glBindVertexArray(meshMaterialSet.mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        if(meshMaterialSet.mesh.isInstanced()){
            GL20.glEnableVertexAttribArray(5);
            GL20.glEnableVertexAttribArray(6);
            GL20.glEnableVertexAttribArray(7);
            GL20.glEnableVertexAttribArray(8);
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
    public void prepare(GameObject entity, Camera camera) { }

    public void prepare(Vector3f lightDirection, ShadowFrustum shadowFrustum){
        updateOrthoProjectionMatrix();
        updateLightViewMatrix(lightDirection, shadowFrustum.getCenter());
        shadowFrustum.update(lightViewMatrix);

        //projectionMatrix.mul(lightViewMatrix, projectionViewMatrix);

        projectionMatrix.mulOrthoAffine(lightViewMatrix, projectionViewMatrix); //If the regular multiply works, try this one too

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_FRONT);
        shadowFrameBuffer.bindFrameBuffer();
    }

    @Override
    public void cleanUp() {
        shadowShader.cleanUp();
    }

    public void queue(RenderComponent renderComponent) {
        renderComponent.getMeshMaterialSets().forEach((meshMaterialSet -> {
            if(meshMaterialSet.material.castShadow()){
                shadowSets.add(meshMaterialSet);
            }
        }));
    }

    public final int getShadowMapID(){
        return shadowFrameBuffer.getShadowMap();
    }

    public final Matrix4f getToShadowMapSpaceMatrix() {
        return new Matrix4f(offset).mul(projectionViewMatrix);
    }

    public void setMainCamera(Camera camera){
        if(shadowFrustum != null) shadowFrustum.setCamera(camera);
    }

    private void updateLightViewMatrix(Vector3f lightDirection, Vector3f center){
        lightViewMatrix.identity().lookAt(
                new Vector3f(center).sub(lightDirection),
                center,
                new Vector3f(0, 1, 0)
        );
    }


    private void updateOrthoProjectionMatrix() {
        projectionMatrix.identity();
        projectionMatrix.m00(2f / shadowFrustum.getWidth());
        projectionMatrix.m11(2f / shadowFrustum.getHeight());
        projectionMatrix.m22(-2f / shadowFrustum.getLength());
        projectionMatrix.m33(1f);
    }

    private static Matrix4f createOffset() {
        return new Matrix4f()
                .translate(0.5f, 0.5f, 0.5f)
                .scale(0.5f, 0.5f, 0.5f);
    }

    public void setMetrics(RenderMetrics metrics){
        this.metrics = metrics;
        recordMetrics = true;
    }

    public void recordMetrics(boolean recordMetrics) {
        this.recordMetrics = recordMetrics;
    }
}
