package nl.jenoah.core.shaders.postProcessing.effects;

import nl.jenoah.core.rendering.ImageRenderer;
import nl.jenoah.core.shaders.postProcessing.PPFXHorizontalBlurShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class PPFXHorizontalBlurEffect {
    private ImageRenderer renderer;
    private PPFXHorizontalBlurShader shader;

    public PPFXHorizontalBlurEffect(int targetFBOWidth, int targetFBOHeight){
        try {
            shader = new PPFXHorizontalBlurShader();
            shader.init();
            shader.setTargetWidth(targetFBOWidth);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        renderer = new ImageRenderer(targetFBOWidth, targetFBOHeight);
    }

    public void render(int textureID){
        shader.bind();
        shader.prepare();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

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
