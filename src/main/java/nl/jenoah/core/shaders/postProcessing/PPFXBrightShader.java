package nl.jenoah.core.shaders.postProcessing;

import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Utils;

public class PPFXBrightShader extends Shader {
    private float threshold = 0.6f;

    public PPFXBrightShader() throws Exception {
        super();
    }

    public void init() throws Exception {
        loadVertexShaderFromFile("/shaders/postProcessing/ppfxGeneric.vs");
        loadFragmentShaderFromFile("/shaders/postProcessing/ppfxBrightFilter.fs");
        link();
        super.init();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        createUniform("threshold");
    }

    public void setThreshold(float threshold){
        this.threshold = threshold;
    }

    @Override
    public void prepare() {
        setUniform("threshold", threshold);
    }
}
