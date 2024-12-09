package nl.jenoah.core.shaders;

import nl.jenoah.core.ModelManager;

public class ShaderManager {

    BlinnPhongShader litShader;
    TerrainShader terrainShader;
    UnlitShader unlitShader;
    BillboardShader billboardShader;
    TriplanarShader triplanarShader;

    private static ShaderManager instance = null;

    public static synchronized ShaderManager getInstance()
    {
        if (instance == null) {
            instance = new ShaderManager();
            instance.init();
        }

        return instance;
    }

    public BlinnPhongShader getLitShader(){
        return litShader;
    }

    public UnlitShader getUnlitShader(){
        return unlitShader;
    }

    public BillboardShader getBillboardShader(){
        return billboardShader;
    }

    public TerrainShader getTerrainShader(){
        return terrainShader;
    }

    public TriplanarShader getTriplanarShader() { return triplanarShader; }

    public void init(){
        try {
            litShader = new BlinnPhongShader();
            litShader.init();

            unlitShader = new UnlitShader();
            unlitShader.init();

            billboardShader = new BillboardShader();
            billboardShader.init();

            terrainShader = new TerrainShader();
            terrainShader.init();

            triplanarShader = new TriplanarShader();
            triplanarShader.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
