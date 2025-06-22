package nl.jenoah.core.shaders;

import nl.jenoah.core.entity.Camera;
import nl.jenoah.core.rendering.MeshMaterialSet;
import nl.jenoah.core.utils.Utils;
import org.lwjgl.opengl.*;

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
        super.prepare(meshMaterialSet, camera);

        this.setUniform("useInstancing", meshMaterialSet.mesh.isInstanced());

        if(meshMaterialSet.material.hasAlbedoTexture()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, meshMaterialSet.material.getAlbedoTexture().getId());
            this.setTexture("albedoMap", 0);
            this.setUniform("hasAlbedoMap", 1);
        }else{
            this.setUniform("hasAlbedoMap", 0);
        }
        if(meshMaterialSet.material.hasNormalMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, meshMaterialSet.material.getNormalMap().getId());
            this.setTexture("normalMap", 1);
            this.setUniform("hasNormalMap", 1);
        }else{
            this.setUniform("hasNormalMap", 0);
        }
        if(meshMaterialSet.material.hasRoughnessMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, meshMaterialSet.material.getRoughnessMap().getId());
            this.setTexture("roughnessMap", 2);
            this.setUniform("hasRoughnessMap", 1);
        }else{
            this.setUniform("hasRoughnessMap", 0);
        }
        if(meshMaterialSet.material.hasMetallicMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE3);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, meshMaterialSet.material.getMetallicMap().getId());
            this.setTexture("metallicMap", 3);
            this.setUniform("hasMetallicMap", 1);
        }else{
            this.setUniform("hasMetallicMap", 0);
        }
        if(meshMaterialSet.material.hasAOMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE4);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, meshMaterialSet.material.getAoMap().getId());
            this.setTexture("aoMap", 4);
            this.setUniform("hasAOMap", 1);
        }else{
            this.setUniform("hasAOMap", 0);
        }
    }
}
