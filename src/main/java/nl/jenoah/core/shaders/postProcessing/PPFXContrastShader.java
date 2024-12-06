package nl.jenoah.core.shaders.postProcessing;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Utils;

public class PPFXContrastShader extends Shader {
    public PPFXContrastShader() throws Exception {
        super();
    }

    public void init() throws Exception {
        createVertexShader(Utils.loadResource("/shaders/postProcessing/ppfxGeneric.vs"));
        createFragmentShader(Utils.loadResource("/shaders/postProcessing/ppfxContrast.fs"));
        link();
        super.init();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
    }

    @Override
    public void prepare(Entity entity, Camera camera) {

    }
}
