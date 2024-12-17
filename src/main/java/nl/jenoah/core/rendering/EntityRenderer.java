package nl.jenoah.core.rendering;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.Material;
import nl.jenoah.core.shaders.Shader;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.entity.Model;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.*;

public class EntityRenderer implements IRenderer{

    private HashMap<Material, List<Entity>> entities;

    public EntityRenderer(){
        entities = new HashMap<>();
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void render(Camera camera) {
        if(entities.isEmpty()) return;
        //Debug.Log("Count is " + entities.size());
        entities.forEach((mat, entList) -> {
            Shader shader = mat.getShader();
            shader.bind();
            shader.render(camera);

            for(Entity e : entList){
                if(!e.isEnabled()) continue;
                bind(e.getModel());
                shader.prepare(e, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, e.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                unbind();
            }
            shader.unbind();
        });
        //entities.clear();
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
        for(Material mat: entities.keySet()) {
            mat.getShader().cleanUp();
        }
    }

    public void addEntity(Entity entity){
        Material mat = entity.getModel().getMaterial();
        if(this.entities.containsKey(mat)){
            this.entities.get(mat).add(entity);
        }else{
            this.entities.put(mat, new ArrayList<>(){{add(entity);}});
        }
    }

    public void setEntities(HashMap<Material, List<Entity>> entities){
        this.entities = entities;
    }

    public final List<Entity> getEntities() {
        List<Entity> entityList = new ArrayList<>();
        entities.forEach((mat, ent) -> {
            entityList.addAll(ent);
        });
        return entityList;
    }
}
