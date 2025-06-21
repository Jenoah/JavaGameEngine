package nl.jenoah.core.shaders;

import nl.jenoah.core.entity.Camera;
import nl.jenoah.core.rendering.MeshMaterialSet;
import nl.jenoah.core.utils.Transformation;
import nl.jenoah.core.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.glDepthMask;

public class DebugShader extends Shader{

    public DebugShader() throws Exception {
        super();
        createVertexShader(Utils.loadResource("/shaders/debug/vertex.vs"));
        createFragmentShader(Utils.loadResource("/shaders/debug/fragment.fs"));
        link();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        createUniform("modelMatrix");
        createUniform("viewMatrix");
        createUniform("projectionMatrix");
    }

    public void prepare(Vector3f position, Quaternionf rotation, Vector3f scale, Camera camera) {
        //glDepthMask(true);

        Matrix4f modelMatrix = Transformation.toModelMatrix(position, rotation, scale);

        this.setUniform("modelMatrix", modelMatrix);
        this.setUniform("viewMatrix", camera.getViewMatrix());
        this.setUniform("projectionMatrix", window.getProjectionMatrix());
    }
}
