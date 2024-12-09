package nl.jenoah.core.gui;

import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Transformation;
import nl.jenoah.core.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class GuiShader extends Shader {

    private Vector4f color = new Vector4f(1f);

    public GuiShader() throws Exception {
        super();
    }

    public void init() throws Exception {
        createVertexShader(Utils.loadResource("/shaders/GUI/guiGeneric.vs"));
        createFragmentShader(Utils.loadResource("/shaders/GUI/guiGeneric.fs"));
        link();
        super.init();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        createUniform("modelMatrix");
        createUniform("uiColor");
        createUniform("hasTexture");
    }

    public void prepare(GuiObject guiObject) {
        Matrix4f modelMatrix = Transformation.getModelMatrix(guiObject);
        setUniform("modelMatrix", modelMatrix);
        setUniform("uiColor", color);
        setUniform("hasTexture", guiObject.getTexture() == -1 ? 0 : 1);
    }

    public void setColor(Vector4f color){
        this.color = color;
    }
}
