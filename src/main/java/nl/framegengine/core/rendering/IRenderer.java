package nl.framegengine.core.rendering;

import nl.framegengine.core.entity.Camera;
import nl.framegengine.core.entity.GameObject;

public interface IRenderer{

    public void init() throws Exception;

    public void render();

    abstract void bind(MeshMaterialSet meshMaterialSet);

    public void unbind();

    public void prepare(GameObject entity, Camera camera);

    public void cleanUp();

}
