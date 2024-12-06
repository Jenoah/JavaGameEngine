package nl.jenoah.core.shaders.postProcessing.effects;

import nl.jenoah.core.rendering.ImageRenderer;
import nl.jenoah.core.shaders.postProcessing.PPFXBrightShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class PPFXBrightEffect {
    private ImageRenderer renderer;
    private PPFXBrightShader shader;
    private float threshold = 0.6f;

    public PPFXBrightEffect(int targetFBOWidth, int targetFBOHeight){
        try {
            shader = new PPFXBrightShader();
            shader.init();
            shader.setThreshold(threshold);
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

    public void setThreshold(float threshold){
        this.threshold = threshold;
        shader.setThreshold(threshold);
    }

    public int getOutputTexture(){
        return renderer.getOutputTexture();
    }

    public void cleanUp(){
        renderer.cleanUp();
        shader.cleanUp();
    }
}
