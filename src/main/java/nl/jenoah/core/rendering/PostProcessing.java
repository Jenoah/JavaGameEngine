package nl.jenoah.core.rendering;

import nl.jenoah.core.WindowManager;
import nl.jenoah.core.entity.Model;
import nl.jenoah.core.loaders.PrimitiveLoader;
import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.shaders.postProcessing.PPFXBrightShader;
import nl.jenoah.core.shaders.postProcessing.PPFXGammaCorrectShader;
import nl.jenoah.core.shaders.postProcessing.PPFXHorizontalBlurShader;
import nl.jenoah.core.shaders.postProcessing.PPFXVerticalBlurShader;
import nl.jenoah.core.shaders.postProcessing.effects.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class PostProcessing {
    private static Model quad;

    private static PPFXGenericEffect horizontalBlurEffectPass1;
    private static PPFXGenericEffect verticalBlurEffectPass1;
    private static PPFXGenericEffect gammaCorrectEffect;
    private static PPFXGenericEffect contrastEffect;
    private static PPFXGenericEffect brightEffect;
    private static PPFXGenericEffect outputEffect;

    private static PPFXCombineEffect combineEffect;

    private static final int bloomSize = 8;
    private static final float bloomThreshold = .95f;
    private static final float bloomIntensity = 1.75f;

    public static void init(){
        quad = getQuad();
        WindowManager window = WindowManager.getInstance();

        //Blurring
        combineEffect = new PPFXCombineEffect(window.getWidth(), window.getHeight());

        try {
            //Blur (for Bloom)
            PPFXHorizontalBlurShader horizontalBlurShader = new PPFXHorizontalBlurShader();
            PPFXVerticalBlurShader verticalBlurShader = new PPFXVerticalBlurShader();
            verticalBlurShader.setTargetHeight(window.getHeight() / bloomSize);
            horizontalBlurShader.setTargetWidth(window.getWidth() / bloomSize);
            horizontalBlurEffectPass1 = new PPFXGenericEffect(window.getWidth() / bloomSize, window.getHeight() / bloomSize, horizontalBlurShader);
            verticalBlurEffectPass1 = new PPFXGenericEffect(window.getWidth() / bloomSize, window.getHeight() / bloomSize, verticalBlurShader);

            //Brightness detection (for Bloom)
            PPFXBrightShader brightShader = new PPFXBrightShader();
            brightShader.setThreshold(bloomThreshold);
            brightEffect = new PPFXGenericEffect(window.getWidth() / 2, window.getHeight() / 2, brightShader); //Divided by 2 due to performance

            //Gamma and contrast adjustments
            gammaCorrectEffect = new PPFXGenericEffect(window.getWidth(), window.getHeight(), new PPFXGammaCorrectShader());
            contrastEffect = new PPFXGenericEffect(window.getWidth(), window.getHeight(), new Shader().init("/shaders/postProcessing/ppfxGeneric.vs", "/shaders/postProcessing/ppfxContrast.fs"));

            //Render final
            outputEffect = new PPFXGenericEffect(new Shader().init("/shaders/postProcessing/ppfxGeneric.vs", "/shaders/postProcessing/ppfxGeneric.fs"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        combineEffect.setIntensity(bloomIntensity);
    }

    private static Model getQuad(){
        float[] vertices = new float[]{
                -1f,  1f, 0.0f, // Top-left
                -1,  -1, 0.0f, // Top-right
                1, 1, 0.0f, // Bottom-right
                1, -1, 0.0f  // Bottom-left
        };

        return PrimitiveLoader.loadModel(vertices);
    }

    public static void render(int colourTexture){
        bind();

        //Bloom
        brightEffect.render(colourTexture);
        horizontalBlurEffectPass1.render(brightEffect.getOutputTexture());
        verticalBlurEffectPass1.render(horizontalBlurEffectPass1.getOutputTexture());
        combineEffect.render(colourTexture, verticalBlurEffectPass1.getOutputTexture());

        //Color adjustment
        contrastEffect.render(combineEffect.getOutputTexture());
        gammaCorrectEffect.render(contrastEffect.getOutputTexture());

        //Render to screen
        outputEffect.render(gammaCorrectEffect.getOutputTexture());

        unbind();
    }

    public static void cleanUp(){
        horizontalBlurEffectPass1.cleanUp();
        verticalBlurEffectPass1.cleanUp();
        brightEffect.cleanUp();
        contrastEffect.cleanUp();
        combineEffect.cleanUp();
        gammaCorrectEffect.cleanUp();
        outputEffect.cleanUp();
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
