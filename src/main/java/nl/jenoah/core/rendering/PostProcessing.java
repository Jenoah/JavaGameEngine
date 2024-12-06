package nl.jenoah.core.rendering;

import game.Launcher;
import nl.jenoah.core.ModelManager;
import nl.jenoah.core.WindowManager;
import nl.jenoah.core.entity.Model;
import nl.jenoah.core.shaders.postProcessing.effects.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class PostProcessing {
    private static Model quad;
    private static PPFXContrastEffect contrastEffect;

    //Blurring
    private static final int bloomSize = 12;
    private static final float bloomThreshold = .95f;
    private static final float bloomIntensity = 1.5f;
    private static PPFXHorizontalBlurEffect horizontalBlurEffectPass1;
    private static PPFXVerticalBlurEffect verticalBlurEffectPass1;
    private static PPFXBrightEffect brightEffect;
    private static PPFXCombineEffect combineEffect;
    private static WindowManager window;

    public static void init(){
        quad = getQuad();
        window = Launcher.getWindow();

        //Blurring
        horizontalBlurEffectPass1 = new PPFXHorizontalBlurEffect(window.getWidth() / bloomSize, window.getHeight() / bloomSize);
        verticalBlurEffectPass1 = new PPFXVerticalBlurEffect(window.getWidth() / bloomSize, window.getHeight() / bloomSize);
        brightEffect = new PPFXBrightEffect(window.getWidth() / 2, window.getHeight() / 2); //Divided by 2 due to performance
        combineEffect = new PPFXCombineEffect();
        contrastEffect = new PPFXContrastEffect(window.getWidth(), window.getHeight());

        brightEffect.setThreshold(bloomThreshold);
        combineEffect.setIntensity(bloomIntensity);
    }

    private static Model getQuad(){
        float[] vertices = new float[]{
                -1f,  1f, 0.0f, // Top-left
                -1,  -1, 0.0f, // Top-right
                1, 1, 0.0f, // Bottom-right
                1, -1, 0.0f  // Bottom-left
        };

        return ModelManager.getInstance().getPrimitiveLoader().loadModel(vertices);
    }

    public static void render(int colourTexture){
        bind();

        //Contrast
        //contrastEffect.render(colourTexture);

        //Bloom
        brightEffect.render(colourTexture);
        horizontalBlurEffectPass1.render(brightEffect.getOutputTexture());
        verticalBlurEffectPass1.render(horizontalBlurEffectPass1.getOutputTexture());
        contrastEffect.render(verticalBlurEffectPass1.getOutputTexture());
        combineEffect.render(colourTexture, contrastEffect.getOutputTexture());

        unbind();
    }

    public static void cleanUp(){
        horizontalBlurEffectPass1.cleanUp();
        verticalBlurEffectPass1.cleanUp();
        brightEffect.cleanUp();
        contrastEffect.cleanUp();
        combineEffect.cleanUp();
    }

    private static void bind(){
        GL30.glBindVertexArray(quad.getId());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private static void unbind(){
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }
}
