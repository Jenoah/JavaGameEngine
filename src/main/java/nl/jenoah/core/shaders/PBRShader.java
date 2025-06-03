package nl.jenoah.core.shaders;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.SceneManager;
import nl.jenoah.core.rendering.MeshMaterialSet;
import nl.jenoah.core.utils.Transformation;
import nl.jenoah.core.utils.Utils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

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
        createUniform("roughnessMap");
        createUniform("hasRoughnessMap");
        createUniform("metallicMap");
        createUniform("hasMetallicMap");
        createUniform("aoMap");
        createUniform("hasAOMap");
        createUniform("useInstancing");
    }

    @Override
    public void prepare(MeshMaterialSet meshMaterialSet, Camera camera) {
        glDepthMask(true);

        Matrix4f modelMatrix = Transformation.getModelMatrix(meshMaterialSet.getRoot());
        Matrix4f viewMatrix = Transformation.getViewMatrix(camera);

        Shader shader = meshMaterialSet.material.getShader();
        shader.setUniform("material", meshMaterialSet.material);
        shader.setUniform("viewMatrix", viewMatrix);
        shader.setUniform("projectionMatrix", window.getProjectionMatrix());
        shader.setUniform("fogColor", SceneManager.fogColor);
        shader.setUniform("fogDensity", SceneManager.fogDensity);
        shader.setUniform("fogGradient", SceneManager.fogGradient);
        if(meshMaterialSet.mesh.isInstanced()){
            shader.setUniform("useInstancing", true);
        }else{
            shader.setUniform("useInstancing", false);
            shader.setUniform("modelMatrix", modelMatrix);
        }

        if(meshMaterialSet.material.hasAlbedoTexture()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, meshMaterialSet.material.getAlbedoTexture().getId());
            shader.setTexture("albedoMap", 0);
            shader.setUniform("hasAlbedoMap", 1);
        }else{
            shader.setUniform("hasAlbedoMap", 0);
        }
        if(meshMaterialSet.material.hasNormalMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, meshMaterialSet.material.getNormalMap().getId());
            shader.setTexture("normalMap", 1);
            shader.setUniform("hasNormalMap", 1);
        }else{
            shader.setUniform("hasNormalMap", 0);
        }
        if(meshMaterialSet.material.hasRoughnessMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, meshMaterialSet.material.getRoughnessMap().getId());
            shader.setTexture("roughnessMap", 1);
            shader.setUniform("hasRoughnessMap", 1);
        }else{
            shader.setUniform("hasRoughnessMap", 0);
        }
        if(meshMaterialSet.material.hasMetallicMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, meshMaterialSet.material.getMetallicMap().getId());
            shader.setTexture("metallicMap", 1);
            shader.setUniform("hasMetallicMap", 1);
        }else{
            shader.setUniform("hasMetallicMap", 0);
        }
        if(meshMaterialSet.material.hasAOMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, meshMaterialSet.material.getAoMap().getId());
            shader.setTexture("aoMap", 1);
            shader.setUniform("hasAOMap", 1);
        }else{
            shader.setUniform("hasAOMap", 0);
        }
    }
}
