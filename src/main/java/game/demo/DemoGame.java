package game.demo;

import nl.jenoah.core.ILogic;
import nl.jenoah.core.MouseInput;
import nl.jenoah.core.WindowManager;
import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.entity.Scene;
import nl.jenoah.core.entity.SceneManager;
import nl.jenoah.core.rendering.RenderManager;

public class DemoGame implements ILogic {
    private final RenderManager renderer;
    private final WindowManager window;
    private final SceneManager sceneManager;

    public DemoGame(){
        renderer = new RenderManager();
        window = WindowManager.getInstance();
        sceneManager = SceneManager.getInstance();

        window.updateProjectionMatrix();
    }

    @Override
    public void init() throws Exception {
        Debug.Log("Initiating game...");
        renderer.init();

        Scene level = new Level0();

        window.setClearColor(0, 0, 0, 0);

        sceneManager.addScene(level);
        sceneManager.setCurrentScene(0);
    }

    @Override
    public void input() {
        sceneManager.getCurrentScene().handleInput();
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {

        sceneManager.getCurrentScene().update(mouseInput);

        for(Entity entity : sceneManager.getCurrentScene().getEntities()){
            renderer.processEntity(entity);
        }
    }

    @Override
    public void render() {
        renderer.render(sceneManager.getCurrentScene());
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        sceneManager.cleanUp();
    }

    public RenderManager getRenderer(){
        return renderer;
    }
}