package nl.jenoah.core.skybox;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.SceneManager;
import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.utils.Constants;
import nl.jenoah.core.utils.Transformation;
import nl.jenoah.core.utils.Utils;
import org.joml.Matrix4f;

public class SkyboxShader extends Shader {

    public SkyboxShader() throws Exception {
        super();
    }

    public void init() throws Exception {
        createVertexShader(Utils.loadResource("/shaders/skybox/skyboxGeneric.vs"));
        createFragmentShader(Utils.loadResource("/shaders/skybox/skyboxGeneric.fs"));
        link();
        super.init();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        createUniform("gamma");
        createUniform("textureSampler");
        createUniform("projectionMatrix");
        createUniform("viewMatrix");
        createUniform("fogColor");
    }

    public void prepare(Camera camera) {
        Matrix4f viewMatrix = Transformation.getViewMatrix(camera);
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        Matrix4f projectionMatrix = window.getProjectionMatrix();

        setUniform("gamma", Constants.GAMMA);
        setUniform("viewMatrix", viewMatrix);
        setUniform("projectionMatrix", projectionMatrix);
        setUniform("fogColor", SceneManager.fogColor);

    }
}
