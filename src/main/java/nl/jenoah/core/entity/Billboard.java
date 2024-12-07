package nl.jenoah.core.entity;

import nl.jenoah.core.shaders.ShaderManager;
import org.joml.Vector3f;

public class Billboard extends Entity{

    public Billboard(Model model, Texture texture) {
        super(model, new Vector3f(0), new Vector3f(0), 1);
        model.getMaterial().setShader(ShaderManager.getInstance().getBillboardShader());
        model.setTexture(texture);
    }

    public Billboard(Model model, Texture texture, float scale) {
        super(model, new Vector3f(0), new Vector3f(0), scale);
        model.getMaterial().setShader(ShaderManager.getInstance().getBillboardShader());
        model.setTexture(texture);
    }
}
