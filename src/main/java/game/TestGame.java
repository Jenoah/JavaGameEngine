package game;

import game.actual.levels.Level1;
import game.demo.levels.Level0;
import nl.jenoah.core.*;
import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.*;
import nl.jenoah.core.rendering.RenderManager;

public class TestGame implements ILogic {
    private final RenderManager renderer;
    private final WindowManager window;
    private final SceneManager sceneManager;
    //private final ShaderManager shaderManager;

    public TestGame(){
        renderer = new RenderManager();
        window = Launcher.getWindow();
        //shaderManager = new ShaderManager();
        sceneManager = SceneManager.getInstance();

        window.updateProjectionMatrix();
    }

    @Override
    public void init() throws Exception {
        Debug.Log("Initiating game...");
        renderer.init();

        Scene level = new Level0();

        //window.setClearColor(0f, .9f, 1f, 0);
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
