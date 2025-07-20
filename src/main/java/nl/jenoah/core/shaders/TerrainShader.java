package nl.jenoah.core.shaders;

import nl.jenoah.core.entity.Camera;
import nl.jenoah.core.rendering.MeshMaterialSet;
import nl.jenoah.core.utils.FileHelper;
import nl.jenoah.core.utils.Utils;
import org.joml.Vector2f;

public class TerrainShader extends Shader {
    private Vector2f terrainHeight = new Vector2f(-1, 1);

    public TerrainShader() throws Exception {
        super();
        loadVertexShaderFromFile("/shaders/terrain/terrain.vs");
        loadFragmentShaderFromFile("/shaders/terrain/terrain.fs");
        link();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        super.createRequiredUniforms();

        createUniform("terrainHeight");
    }

    @Override
    public void prepare(MeshMaterialSet meshMaterialSet, Camera camera) {
        super.prepare(meshMaterialSet, camera);
        setUniform("terrainHeight", terrainHeight);
    }

    public void setTerrainHeight(Vector2f terrainHeight){
        this.terrainHeight = terrainHeight;
    }
}
