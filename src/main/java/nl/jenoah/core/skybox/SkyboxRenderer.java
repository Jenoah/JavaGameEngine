package nl.jenoah.core.skybox;

import nl.jenoah.core.Camera;
import nl.jenoah.core.ModelManager;
import nl.jenoah.core.entity.Model;
import nl.jenoah.core.loaders.TextureLoader;
import org.lwjgl.opengl.*;

public class SkyboxRenderer {
    private static final float SIZE = 500f;
    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };

    private final Model cube;
    private final int textureID;
    private final SkyboxShader shader;

    public SkyboxRenderer(String[] textureFiles){
        cube = ModelManager.loadModel(VERTICES, 3);
        textureID = TextureLoader.loadCubeMapTexture(textureFiles);

        try {
            shader = new SkyboxShader();
            shader.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void render(Camera camera){
        prepare();
        shader.prepare(camera);
        GL30.glBindVertexArray(cube.getId());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        endRendering();
    }

    public void cleanUp(){
        shader.cleanUp();
    }

    private void prepare(){
        GL11.glDepthMask(false);
        //GL11.glDepthRange(1f, 1f);
        shader.bind();
    }

    private void endRendering(){
        //GL11.glDepthRange(0f, 1f);
        GL11.glDepthMask(true);
        shader.unbind();
    }
}
