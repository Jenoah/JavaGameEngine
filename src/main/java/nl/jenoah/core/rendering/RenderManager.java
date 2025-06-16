package nl.jenoah.core.rendering;

import nl.jenoah.core.components.RenderComponent;
import nl.jenoah.core.debugging.RenderMetrics;
import nl.jenoah.core.fonts.fontRendering.FontRenderer;
import nl.jenoah.core.gui.GuiRenderer;
import nl.jenoah.core.WindowManager;
import nl.jenoah.core.entity.Scene;
import nl.jenoah.core.skybox.SkyboxRenderer;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glViewport;

public class RenderManager {
    private final WindowManager window;
    private ComponentRenderer componentRenderer;
    public ShadowRenderer shadowRenderer;
    private GuiRenderer guiRenderer;
    private FontRenderer fontRenderer;
    private FrameBuffer frameBuffer;
    private SkyboxRenderer skyboxRenderer;
    private final RenderMetrics metrics;
    private boolean recordMetrics = false;

    public RenderManager() {
        window = WindowManager.getInstance();
        metrics = new RenderMetrics();
    }

    public void init() throws Exception {
        componentRenderer = new ComponentRenderer();
        shadowRenderer = new ShadowRenderer();

        guiRenderer = new GuiRenderer();
        fontRenderer = new FontRenderer();
        skyboxRenderer = new SkyboxRenderer(new String[]{"textures/skyboxes/clouds1/right.png", "textures/skyboxes/clouds1/left.png", "textures/skyboxes/clouds1/top.png", "textures/skyboxes/clouds1/bottom.png", "textures/skyboxes/clouds1/back.png", "textures/skyboxes/clouds1/front.png"});
        componentRenderer.init();
        shadowRenderer.init();
        componentRenderer.setShadowMapID(shadowRenderer.getShadowMapID());

        regenerateFrameBuffer();
        PostProcessing.init();
    }

    public void render(Scene currentScene){
        clear();
        if (recordMetrics) metrics.frameStart();

        if(window.isResize()){
            glViewport(0, 0, window.getWidth(), window.getHeight());
            regenerateFrameBuffer();
            window.setResize(false);
            window.updateProjectionMatrix();
            PostProcessing.updateResolution();
        }

        shadowRenderer.render(currentScene);
        componentRenderer.setShadowSpaceMatrix(shadowRenderer.getToShadowMapSpaceMatrix());

        //3D rendering
        frameBuffer.bindFrameBuffer();
        clear();

        //Rendering of scene
        skyboxRenderer.render(currentScene.getPlayer().getCamera());
        componentRenderer.render(currentScene.getPlayer().getCamera());

        frameBuffer.unbindFrameBuffer();

        //Post Processing
        PostProcessing.render(frameBuffer.getColourTexture());
        //End of 3D rendering

        //Overlay
        guiRenderer.render(currentScene.getGuiObjects());
        fontRenderer.render(currentScene.getTextObjects());

        if (recordMetrics) metrics.frameEnd();
    }

    public void clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanUp(){
        PostProcessing.cleanUp();
        componentRenderer.cleanUp();
        frameBuffer.cleanUp();
        guiRenderer.cleanUp();
        fontRenderer.cleanUp();
        skyboxRenderer.cleanUp();
        shadowRenderer.cleanUp();
    }

    private void regenerateFrameBuffer(){
        frameBuffer = new FrameBuffer(window.getWidth(), window.getHeight(), FrameBuffer.DEPTH_RENDER_BUFFER);
    }

    public void queueRender(RenderComponent renderComponent){
        componentRenderer.queue(renderComponent);
        shadowRenderer.queue(renderComponent);
    }

    public void dequeueRender(RenderComponent renderComponent){
        componentRenderer.dequeue(renderComponent);
    }

    public void recordMetrics(boolean recordState){
        this.recordMetrics = recordState;
        if(recordMetrics){
            componentRenderer.setMetrics(this.metrics);
            shadowRenderer.setMetrics(this.metrics);
            guiRenderer.setMetrics(this.metrics);
            fontRenderer.setMetrics(this.metrics);
        }
        componentRenderer.recordMetrics(recordMetrics);
        shadowRenderer.recordMetrics(recordMetrics);
        guiRenderer.recordMetrics(recordMetrics);
        fontRenderer.recordMetrics(recordMetrics);
    }

    public String getMetrics() {
        return recordMetrics ? metrics.getMetrics() : "Metrics not recorded";
    }
}
