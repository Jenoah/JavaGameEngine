package nl.jenoah.core.shaders.postProcessing.effects;

import nl.jenoah.core.rendering.ImageRenderer;
import nl.jenoah.core.shaders.postProcessing.PPFXVerticalBlurShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class PPFXVerticalBlurEffect {
    private ImageRenderer renderer;
    private PPFXVerticalBlurShader shader;

    public PPFXVerticalBlurEffect(int targetFBOWidth, int targetFBOHeight){
        try {
            shader = new PPFXVerticalBlurShader();
            shader.init();
            shader.setTargetHeight(targetFBOHeight);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        renderer = new ImageRenderer(targetFBOWidth, targetFBOHeight);
    }

    public void render(int textureID){
        shader.bind();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        shader.prepare();
        renderer.renderQuad();

        shader.unbind();
    }

    public int getOutputTexture(){
        return renderer.getOutputTexture();
    }

    public void cleanUp(){
        renderer.cleanUp();
        shader.cleanUp();
    }
}
