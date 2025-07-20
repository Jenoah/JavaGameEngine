package nl.jenoah.core.shaders.postProcessing;

import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Utils;

public class PPFXCombineShader extends Shader {

    private float secondaryIntensity = 1f;

    public PPFXCombineShader() throws Exception {
        super();
    }

    public void init() throws Exception {
        loadVertexShaderFromFile("/shaders/postProcessing/ppfxGeneric.vs");
        loadFragmentShaderFromFile("/shaders/combineTextures.fs");
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

    @Override
    public void prepare() {
        super.prepare();
        this.setUniform("secondaryIntensity", secondaryIntensity);
    }
}
