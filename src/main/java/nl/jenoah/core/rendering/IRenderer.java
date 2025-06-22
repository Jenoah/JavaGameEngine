package nl.jenoah.core.rendering;

import nl.jenoah.core.entity.Camera;
import nl.jenoah.core.entity.GameObject;

public interface IRenderer{

    public void init() throws Exception;

    public void render();

    abstract void bind(MeshMaterialSet meshMaterialSet);

    public void unbind();

    public void prepare(GameObject entity, Camera camera);

    public void cleanUp();

}
