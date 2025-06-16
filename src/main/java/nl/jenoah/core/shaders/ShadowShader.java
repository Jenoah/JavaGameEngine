package nl.jenoah.core.shaders;
import nl.jenoah.core.rendering.MeshMaterialSet;
import nl.jenoah.core.utils.Transformation;
import nl.jenoah.core.utils.Utils;
import org.joml.Matrix4f;

public class ShadowShader extends Shader{
    public ShadowShader() throws Exception {
        super();
        createVertexShader(Utils.loadResource("/shaders/shadow/vertex.vs"));
        createFragmentShader(Utils.loadResource("/shaders/shadow/fragment.fs"));
        link();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        //createUniform("modelTexture");
        createUniform("modelMatrix");
        createUniform("projectionViewMatrix");
        createUniform("useInstancing");
    }

    public void prepare(MeshMaterialSet meshMaterialSet, Matrix4f projectionViewMatrix) {
        Matrix4f modelMatrix = Transformation.getModelMatrix(meshMaterialSet.getRoot());

        this.setUniform("projectionViewMatrix", projectionViewMatrix);

        if(meshMaterialSet.mesh.isInstanced()){
            this.setUniform("useInstancing", true);
        }else{
            this.setUniform("useInstancing", false);
            this.setUniform("modelMatrix", modelMatrix);
        }
    }
}
