package nl.jenoah.core.shaders;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.utils.Utils;
import org.joml.Vector2f;

public class TerrainShader extends BlinnPhongShader{
    private Vector2f terrainHeight = new Vector2f(-1, 1);

    public TerrainShader() throws Exception {
        super();
    }

    @Override
    public void init() throws Exception {
        createVertexShader(Utils.loadResource("/shaders/terrain/terrain.vs"));
        createFragmentShader(Utils.loadResource("/shaders/terrain/terrain.fs"));
        link();

        createRequiredUniforms();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        super.createRequiredUniforms();

        createUniform("terrainHeight");
    }

    @Override
    public void prepare(Entity entity, Camera camera) {
        super.prepare(entity, camera);
        setUniform("terrainHeight", terrainHeight);
    }

    public void setTerrainHeight(Vector2f terrainHeight){
        this.terrainHeight = terrainHeight;
    }
}
