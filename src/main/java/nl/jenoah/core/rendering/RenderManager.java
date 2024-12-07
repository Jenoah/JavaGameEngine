package nl.jenoah.core.rendering;

import nl.jenoah.core.ModelManager;
import nl.jenoah.core.fonts.fontRendering.FontRenderer;
import nl.jenoah.core.gui.GuiRenderer;
import nl.jenoah.core.WindowManager;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.entity.Scene;
import nl.jenoah.core.skybox.SkyboxRenderer;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glViewport;

public class RenderManager {
    private final WindowManager window;
    private EntityRenderer entityRenderer;
    private EntityRenderer transparentEntityRenderer;
    private GuiRenderer guiRenderer;
    private FontRenderer fontRenderer;
    private FrameBuffer frameBuffer;
    private SkyboxRenderer skyboxRenderer;

    public RenderManager() {
        window = WindowManager.getInstance();
    }

    public void init() throws Exception {
        entityRenderer = new EntityRenderer();
        transparentEntityRenderer = new EntityRenderer();

        guiRenderer = new GuiRenderer(ModelManager.getInstance().getGuiLoader());
        fontRenderer = new FontRenderer();
        skyboxRenderer = new SkyboxRenderer(new String[]{"textures/skyboxes/clouds1/right.png", "textures/skyboxes/clouds1/left.png", "textures/skyboxes/clouds1/top.png", "textures/skyboxes/clouds1/bottom.png", "textures/skyboxes/clouds1/back.png", "textures/skyboxes/clouds1/front.png"});

        entityRenderer.init();
        transparentEntityRenderer.init();

        regenerateFrameBuffer();
        PostProcessing.init();
    }

    public void render(Scene currentScene){
        clear();

        if(window.isResize()){
            glViewport(0, 0, window.getWidth(), window.getHeight());
            regenerateFrameBuffer();
            window.setResize(false);
            window.updateProjectionMatrix();
        }

        //3D rendering
        frameBuffer.bindFrameBuffer();
        clear();

        //Rendering of scene
        entityRenderer.render(currentScene.getPlayer().getCamera());
        skyboxRenderer.render(currentScene.getPlayer().getCamera());
        transparentEntityRenderer.render(currentScene.getPlayer().getCamera());
        frameBuffer.unbindFrameBuffer();

        //Post Processing
        PostProcessing.render(frameBuffer.getColourTexture());

        //End of 3D rendering

        //Overlay
        guiRenderer.render(currentScene.getGuiObjects());
        fontRenderer.render(currentScene.getTextObjects());
    }

    public void processEntity(Entity entity){
        if(!entity.isEnabled()) return;
        if(entity.isTransparent()){
            transparentEntityRenderer.getEntities().add(entity);
        }else {
            entityRenderer.getEntities().add(entity);
        }
    }

    public void clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanUp(){
        PostProcessing.cleanUp();
        entityRenderer.cleanUp();
        transparentEntityRenderer.cleanUp();
        frameBuffer.cleanUp();
        guiRenderer.cleanUp();
        fontRenderer.cleanUp();
        skyboxRenderer.cleanUp();
    }

    private void regenerateFrameBuffer(){
        frameBuffer = new FrameBuffer(window.getWidth(), window.getHeight(), FrameBuffer.DEPTH_RENDER_BUFFER);
    }
}
