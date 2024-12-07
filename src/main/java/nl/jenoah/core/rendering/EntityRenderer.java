package nl.jenoah.core.rendering;

import nl.jenoah.core.Camera;
import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.entity.Model;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.*;

public class EntityRenderer implements IRenderer{

    private final List<Entity> entities;

    public EntityRenderer(){
        entities = new ArrayList<>();
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void render(Camera camera) {
        for(Entity entity: entities) {
            Shader shader = entity.getModel().getMaterial().getShader();

            shader.bind();
            shader.render(camera);

            bind(entity.getModel());
            shader.prepare(entity, camera);
            GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbind();

            shader.unbind();
        }
        entities.clear();
    }

    @Override
    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if(model.isDoubleSided()){
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_CULL_FACE);
        }else{
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
        model.getMaterial().getShader().setUniform("material", model.getMaterial());
        if(model.getTexture() != null) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
        }else{
            model.getMaterial().getShader().setUniform("material.hasTexture", 0);
        }
    }

    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(Entity entity, Camera camera) {

    }

    @Override
    public void cleanUp() {
        for(Entity entity: entities) {
            entity.getModel().getMaterial().getShader().cleanUp();
        }
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
