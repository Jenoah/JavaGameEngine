package nl.jenoah.core.shaders;

import nl.jenoah.core.entity.Camera;
import nl.jenoah.core.entity.SceneManager;
import nl.jenoah.core.rendering.MeshMaterialSet;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class BillboardShader extends Shader{
    public BillboardShader() throws Exception {
        super();
        loadVertexShaderFromFile("/shaders/billboard/billboard.vs");
        loadFragmentShaderFromFile("/shaders/billboard/billboard.fs");
        link();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        createMaterialUniform("material");
        createUniform("textureSampler");
        createUniform("viewMatrix");
        createUniform("textureSampler");
        createUniform("objectPosition");
        createUniform("projectionMatrix");
        createUniform("modelScale");
        createUniform("fogColor");
        createUniform("fogDensity");
        createUniform("fogGradient");
    }

    @Override
    public void prepare(MeshMaterialSet meshMaterialSet, Camera camera) {
        Matrix4f projectionMatrix = window.getProjectionMatrix();

        this.setUniform("material", meshMaterialSet.material);
        this.setUniform("objectPosition", meshMaterialSet.getRoot().getPosition());
        this.setUniform("projectionMatrix", projectionMatrix);
        this.setUniform("modelScale", meshMaterialSet.getRoot().getScale());
        this.setUniform("viewMatrix", camera.getViewMatrix());
        this.setUniform("fogColor", SceneManager.fogColor);
        this.setUniform("fogDensity", SceneManager.fogDensity);
        this.setUniform("fogGradient", SceneManager.fogGradient);

        if(meshMaterialSet.material.getAlbedoTexture() != null){
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, meshMaterialSet.material.getAlbedoTexture().getId());
            this.setTexture("textureSampler", 0);
        }
    }
}
