package nl.jenoah.core.shaders;

public class ShaderManager {

    SimpleLitShader litShader;
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

    public SimpleLitShader getLitShader(){
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
            litShader = new SimpleLitShader();
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
