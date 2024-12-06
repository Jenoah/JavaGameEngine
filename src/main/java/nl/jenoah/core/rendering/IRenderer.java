package nl.jenoah.core.rendering;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.entity.Model;

public interface IRenderer<T>{

    public void init() throws Exception;

    public void render(Camera camera);

    abstract void bind(Model model);

    public void unbind();

    public void prepare(Entity entity, Camera camera);

    public void cleanUp();

}
