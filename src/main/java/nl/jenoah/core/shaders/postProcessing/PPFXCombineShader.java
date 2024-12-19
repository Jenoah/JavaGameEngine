package nl.jenoah.core.shaders.postProcessing;

import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Utils;
import org.lwjgl.opengl.GL30;

public class PPFXCombineShader extends Shader {
    private float secondaryIntensity = 1;

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

    public void setPrimaryTextureID(){
        int primaryTextureLocation = GL30.glGetUniformLocation(programID, "primaryTexture");
        setUniform(primaryTextureLocation, 0);
    }

    public void setSecondaryTextureID(){
        int secondaryTextureLocation = GL30.glGetUniformLocation(programID, "secondaryTexture");
        setUniform(secondaryTextureLocation, 1);
    }

    @Override
    public void prepare() {
        this.setUniform("secondaryIntensity", secondaryIntensity);

    }
}
