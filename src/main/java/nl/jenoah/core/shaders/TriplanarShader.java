package nl.jenoah.core.shaders;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class TriplanarShader extends SimpleLitShader {

    private int topTextureID, sideTextureID;
    private float blendFactor = 0.5f;

    public TriplanarShader(int topTextureID, int sideTextureID) throws Exception {
        super();
        setTextureIDs(topTextureID, sideTextureID);
    }

    public TriplanarShader() throws Exception {
        super();
    }

    @Override
    public void init() throws Exception {
        createVertexShader(Utils.loadResource("/shaders/triplanar.vs"));
        createFragmentShader(Utils.loadResource("/shaders/triplanar.fs"));
        link();

        createRequiredUniforms();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        super.createRequiredUniforms();

        //createUniform("topTexture");
        createUniform("sideTexture");
        createUniform("blendFactor");
    }

    @Override
    public void prepare(Entity entity, Camera camera) {
        super.prepare(entity, camera);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, topTextureID);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, sideTextureID);

        setUniform("textureSampler", 0);
        setUniform("sideTexture", 1);
        setUniform("blendFactor", blendFactor);
    }

    public void setTextureIDs(int topTextureID, int sideTextureID){
        this.topTextureID = topTextureID;
        this.sideTextureID = sideTextureID;
    }

    public void setBlendFactor(float blendFactor){
        this.blendFactor = blendFactor;
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        //UNBIND TEXTURES?
    }
}
