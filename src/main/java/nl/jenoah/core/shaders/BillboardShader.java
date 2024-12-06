package nl.jenoah.core.shaders;

import game.Launcher;
import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.entity.SceneManager;
import nl.jenoah.core.utils.Constants;
import nl.jenoah.core.utils.Transformation;
import nl.jenoah.core.utils.Utils;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.glDepthMask;

public class BillboardShader extends Shader{
    public BillboardShader() throws Exception {
        super();
    }

    public void init() throws Exception {
        createVertexShader(Utils.loadResource("/shaders/billboard/billboard.vs"));
        createFragmentShader(Utils.loadResource("/shaders/billboard/billboard.fs"));
        link();
        super.init();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        createMaterialUniform("material");
        createUniform("gamma");
        createUniform("viewMatrix");
        createUniform("textureSampler");
        createUniform("objectPosition");
        createUniform("projectionMatrix");
        createUniform("fogColor");
        createUniform("fogDensity");
    }

    @Override
    public void prepare(Entity entity, Camera camera) {
        Matrix4f viewMatrix = Transformation.getViewMatrix(camera);
        Matrix4f projectionMatrix = Launcher.getWindow().getProjectionMatrix();

        Shader shader = entity.getModel().getMaterial().getShader();
        shader.setUniform("gamma", Constants.GAMMA);
        shader.setUniform("objectPosition", entity.getPosition());
        shader.setUniform("projectionMatrix", projectionMatrix);
        shader.setUniform("viewMatrix", viewMatrix);
        shader.setUniform("textureSampler", 0);
        shader.setUniform("fogColor", SceneManager.fogColor);
        shader.setUniform("fogDensity", SceneManager.fogDensity);
    }
}
