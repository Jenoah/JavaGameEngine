package nl.framegengine.core.shaders;

public class ShaderManager {

    public final static SimpleLitShader litShader;
    public final static UnlitShader unlitShader;
    public final static BillboardShader billboardShader;
    public final static TerrainShader terrainShader;
    public final static TriplanarShader triplanarShader;
    public final static PBRShader pbrShader;

    static {
        try {
            litShader = new SimpleLitShader();
            litShader.init();
            pbrShader = new PBRShader();
            pbrShader.init();
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

    public static void updateGenericUniforms(){
        if(litShader != null) litShader.updateGenericUniforms();
        if(pbrShader != null) pbrShader.updateGenericUniforms();
        if(triplanarShader != null) triplanarShader.updateGenericUniforms();
        if(unlitShader != null) unlitShader.updateGenericUniforms();
        if(billboardShader != null) billboardShader.updateGenericUniforms();
    }
}
