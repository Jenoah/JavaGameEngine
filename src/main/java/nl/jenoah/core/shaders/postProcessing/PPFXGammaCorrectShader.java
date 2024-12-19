package nl.jenoah.core.shaders.postProcessing;

import nl.jenoah.core.Settings;
import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Utils;

public class PPFXGammaCorrectShader extends Shader {

    public PPFXGammaCorrectShader() throws Exception {
        super();
    }

    public void init() throws Exception {
        createVertexShader(Utils.loadResource("/shaders/postProcessing/ppfxGeneric.vs"));
        createFragmentShader(Utils.loadResource("/shaders/postProcessing/ppfxGammaCorrect.fs"));
        link();
        super.init();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        createUniform("gamma");
    }

    @Override
    public void prepare() {
        setUniform("gamma", Settings.getGamma());
    }
}
