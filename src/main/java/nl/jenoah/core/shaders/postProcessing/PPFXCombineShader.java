package nl.jenoah.core.shaders.postProcessing;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Utils;

public class PPFXCombineShader extends Shader {
    private float secondaryIntensity = 1;
    private int primaryTextureID;
    private int secondaryTextureID;

    public PPFXCombineShader() throws Exception {
        super();
    }

    public void init() throws Exception {
        createVertexShader(Utils.loadResource("/shaders/postProcessing/ppfxGeneric.vs"));
        createFragmentShader(Utils.loadResource("/shaders/combineTextures.fs"));
        link();
        super.init();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        createUniform("secondaryIntensity");
        createUniform("primaryTexture");
        createUniform("secondaryTexture");
    }

    public void setIntensity(float secondaryIntensity){
        this.secondaryIntensity = secondaryIntensity;
    }

    public void setPrimaryTextureID(int primaryTextureID){
        this.primaryTextureID = primaryTextureID;
    }

    public void setSecondaryTextureID(int secondaryTextureID){
        this.secondaryTextureID = secondaryTextureID;
    }

    @Override
    public void prepare() {
        this.setUniform("secondaryIntensity", secondaryIntensity);
        this.setUniform("primaryTexture", primaryTextureID);
        this.setUniform("secondaryTexture", secondaryTextureID);
    }
}
