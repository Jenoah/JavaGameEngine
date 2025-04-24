package nl.jenoah.core.rendering;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.GameObject;
import nl.jenoah.core.entity.Model;

public interface IRenderer<T>{

    public void init() throws Exception;

    public void render(Camera camera);

    abstract void bind(MeshMaterialSet meshMaterialSet);

    public void unbind();

    public void prepare(GameObject entity, Camera camera);

    public void cleanUp();

}
