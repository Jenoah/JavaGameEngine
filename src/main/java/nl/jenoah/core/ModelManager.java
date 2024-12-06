package nl.jenoah.core;

import nl.jenoah.core.loaders.*;
import nl.jenoah.core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelManager {
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();

    private OBJLoader objLoader;
    private TextureLoader textureLoader = new TextureLoader();
    private PrimitiveLoader primitiveLoader;
    private RAWLoader rawLoader;
    private GUILoader guiLoader;
    private FontLoader fontLoader;

    private static ModelManager instance = null;

    public static synchronized ModelManager getInstance()
    {
        if (instance == null) {
            instance = new ModelManager();
            instance.init();
        }

        return instance;
    }

    private void init(){
        objLoader = new OBJLoader();
        primitiveLoader = new PrimitiveLoader();
        rawLoader = new RAWLoader();
        guiLoader = new GUILoader();
        fontLoader = new FontLoader();
    }

    public int createVAO(){
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    public void StoreIndicesBuffer(int[] indices){
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
    }

    public void storeDataInAttributeList(int attributeNumber, int vertexCount, float[] data){
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void unbind(){
        GL30.glBindVertexArray(0);
    }

    public void cleanUp(){
        for(int vao: vaos){
            GL30.glDeleteVertexArrays(vao);
        }for(int vbo: vbos){
            GL30.glDeleteBuffers(vbo);
        }
        textureLoader.cleanUp();
    }

    public OBJLoader getObjLoader() {
        return objLoader;
    }

    public PrimitiveLoader getPrimitiveLoader() {
        return primitiveLoader;
    }

    public RAWLoader getRAWLoader() {
        return rawLoader;
    }

    public TextureLoader getTextureLoader() {
        return textureLoader;
    }

    public GUILoader getGuiLoader(){
        return guiLoader;
    }

    public FontLoader getFontLoader() {
        return fontLoader;
    }
}
