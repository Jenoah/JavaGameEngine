package nl.jenoah.core.fonts.fontRendering;

import nl.jenoah.core.shaders.Shader;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class FontShader extends Shader {
    private Vector3f color = new Vector3f(1);
    private Vector2f position = new Vector2f(0);

    public FontShader() throws Exception {
        super();
    }

    public void init() throws Exception {
        loadVertexShaderFromFile("/shaders/GUI/fonts/fontGeneric.vs");
        loadFragmentShaderFromFile("/shaders/GUI/fonts/fontGeneric.fs");
        link();
        super.init();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        createUniform("textColor");
        createUniform("translation");
    }

    public void prepare() {
        setUniform("translation", position);
        setUniform("textColor", color);
    }

    public void setColor(Vector3f color){
        this.color = color;
    }

    public void setPosition(Vector2f position){
        this.position = position;
    }
}
