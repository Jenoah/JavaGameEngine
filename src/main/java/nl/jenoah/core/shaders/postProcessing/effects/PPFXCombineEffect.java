package nl.jenoah.core.shaders.postProcessing.effects;

import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.rendering.ImageRenderer;
import nl.jenoah.core.shaders.postProcessing.PPFXCombineShader;
import nl.jenoah.core.shaders.postProcessing.PPFXContrastShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class PPFXCombineEffect {
    private ImageRenderer renderer;
    private PPFXCombineShader shader;

    public PPFXCombineEffect(){
        try {
            shader = new PPFXCombineShader();
            shader.init();
            shader.setIntensity(10);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        renderer = new ImageRenderer();
    }

    public PPFXCombineEffect(int targetFBOWidth, int targetFBOHeight){
        try {
            shader = new PPFXCombineShader();
            shader.init();
            shader.setIntensity(10);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        renderer = new ImageRenderer(targetFBOWidth, targetFBOHeight);
    }

    public void render(int primaryTextureID, int secondaryTextureID){
        shader.bind();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, primaryTextureID);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, secondaryTextureID);

        //Debug.Log("DO THING ABOVE IN THE PPFXCOMBINEEFFECT CLASS TO THE TRIPLANAR SHADER TEXTURE LOADING?");

        shader.setPrimaryTextureID(0);
        shader.setSecondaryTextureID(1);

        shader.prepare();
        renderer.renderQuad();

        shader.unbind();
    }

    public void setIntensity(float intensity){
        shader.setIntensity(intensity);
    }

    public int getOutputTexture(){
        return renderer.getOutputTexture();
    }

    public void cleanUp(){
        renderer.cleanUp();
        shader.cleanUp();
    }
}
