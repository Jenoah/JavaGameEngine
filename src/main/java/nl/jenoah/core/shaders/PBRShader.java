package nl.jenoah.core.shaders;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.entity.Material;
import nl.jenoah.core.entity.SceneManager;
import nl.jenoah.core.utils.Transformation;
import nl.jenoah.core.utils.Utils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import static org.lwjgl.opengl.GL11.glDepthMask;

public class PBRShader extends SimpleLitShader{
    public PBRShader() throws Exception {
        super();
        createVertexShader(Utils.loadResource("/shaders/lit/PBR/vertex.vs"));
        createFragmentShader(Utils.loadResource("/shaders/lit/PBR/fragment.fs"));
        link();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        super.createRequiredUniforms();
        createUniform("albedoMap");
        createUniform("hasAlbedoMap");
        createUniform("normalMap");
        createUniform("hasNormalMap");
    }

    @Override
    public void prepare(Entity entity, Camera camera) {
        glDepthMask(true);

        Matrix4f modelMatrix = Transformation.getModelMatrix(entity);
        Matrix4f viewMatrix = Transformation.getViewMatrix(camera);

        Material mat = entity.getModel().getMaterial();
        Shader shader = mat.getShader();
        shader.setUniform("material", mat);
        shader.setUniform("modelMatrix", modelMatrix);
        //shader.setUniform("textureSampler", 0);
        shader.setUniform("viewMatrix", viewMatrix);
        shader.setUniform("projectionMatrix", window.getProjectionMatrix());
        shader.setUniform("fogColor", SceneManager.fogColor);
        shader.setUniform("fogDensity", SceneManager.fogDensity);
        shader.setUniform("fogGradient", SceneManager.fogGradient);


        if(mat.hasAlbedoTexture()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getAlbedoTexture().getId());
            shader.setTexture("albedoMap", 0);
            shader.setUniform("hasAlbedoMap", 1);
        }else{
            shader.setUniform("hasAlbedoMap", false);
        }
        if(mat.hasNormalMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getNormalMap().getId());
            shader.setTexture("normalMap", 1);
            shader.setUniform("hasNormalMap", true);
        }else{
            shader.setUniform("hasNormalMap", false);
        }

        if(mat.getAlbedoTexture() != null){
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getAlbedoTexture().getId());
            shader.setTexture("textureSampler", 2);
        }
    }
}
