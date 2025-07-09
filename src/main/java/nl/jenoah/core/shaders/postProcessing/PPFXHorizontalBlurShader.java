package nl.jenoah.core.shaders.postProcessing;

import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Utils;

public class PPFXHorizontalBlurShader extends Shader {
    private int targetWidth = 1280;

    public PPFXHorizontalBlurShader() throws Exception {
        super();
    }

    public void init() throws Exception {
        createVertexShader(Utils.loadResource("/shaders/postProcessing/ppfxHorizontalBlur.vs"));
        createFragmentShader(Utils.loadResource("/shaders/postProcessing/ppfxBlur.fs"));
        link();
        super.init();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        createUniform("targetWidth");
    }

    public void setTargetWidth(int targetWidth){
        this.targetWidth = targetWidth;
    }

    @Override
    public void prepare() {
        this.setUniform("targetWidth", targetWidth);
    }
}
