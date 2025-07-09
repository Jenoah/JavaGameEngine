package nl.jenoah.core;

import nl.jenoah.core.rendering.RenderManager;

public interface ILogic {
    void init() throws Exception;
    void input();
    void update(float interval, MouseInput mouseInput);
    void render();
    void cleanUp();
    RenderManager getRenderer();
}
