package nl.jenoah.core.shaders;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.SceneManager;
import nl.jenoah.core.rendering.MeshMaterialSet;
import nl.jenoah.core.utils.Transformation;
import nl.jenoah.core.utils.Utils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class BillboardShader extends Shader{
    public BillboardShader() throws Exception {
        super();
        createVertexShader(Utils.loadResource("/shaders/billboard/billboard.vs"));
        createFragmentShader(Utils.loadResource("/shaders/billboard/billboard.fs"));
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
        createUniform("fogColor");
        createUniform("fogDensity");
        createUniform("fogGradient");
    }

    @Override
    public void prepare(MeshMaterialSet meshMaterialSet, Camera camera) {
        Matrix4f viewMatrix = Transformation.getViewMatrix(camera);
        Matrix4f projectionMatrix = window.getProjectionMatrix();

        Shader shader = meshMaterialSet.material.getShader();
        shader.setUniform("material", meshMaterialSet.material);
        shader.setUniform("objectPosition", meshMaterialSet.getRoot().getPosition());
        shader.setUniform("projectionMatrix", projectionMatrix);
        shader.setUniform("viewMatrix", viewMatrix);
        //shader.setUniform("textureSampler", 0);
        shader.setUniform("fogColor", SceneManager.fogColor);
        shader.setUniform("fogDensity", SceneManager.fogDensity);
        shader.setUniform("fogGradient", SceneManager.fogGradient);

        if(meshMaterialSet.material.getAlbedoTexture() != null){
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, meshMaterialSet.material.getAlbedoTexture().getId());
            shader.setTexture("textureSampler", 0);
        }
    }
}
