package nl.jenoah.core.shaders.postProcessing;

import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Utils;

public class PPFXVerticalBlurShader extends Shader {
    private int targetHeight = 720;

    public PPFXVerticalBlurShader() throws Exception {
        super();
    }

    public void init() throws Exception {
        createVertexShader(Utils.loadResource("/shaders/postProcessing/ppfxVerticalBlur.vs"));
        createFragmentShader(Utils.loadResource("/shaders/postProcessing/ppfxBlur.fs"));
        link();
        super.init();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        createUniform("targetHeight");
    }

    public void setTargetHeight(int targetHeight){
        this.targetHeight = targetHeight;
    }

    @Override
    public void prepare() {
        setUniform("targetHeight", targetHeight);
    }
}
