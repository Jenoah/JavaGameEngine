package nl.jenoah.core;

import nl.jenoah.core.entity.Mesh;
import nl.jenoah.core.entity.Model;
import nl.jenoah.core.loaders.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import java.util.HashMap;

public class ModelManager {
    private static final HashMap<Integer, Mesh> meshes = new HashMap<>();

    public static Model loadModel(Vector3f[] vertices, Vector2f[] uvs, int[] indices, Vector3f[] normals){
        Mesh mesh = new Mesh(vertices, uvs, indices, normals);
        meshes.put(mesh.getVaoID(), mesh);
        return new Model(mesh);
    }
    public static Model loadModel(Vector2f[] vertices){
        Mesh mesh = new Mesh(vertices);
        meshes.put(mesh.getVaoID(), mesh);
        return new Model(mesh);
    }
    public static Model loadModel(Vector3f[] vertices){
        Mesh mesh = new Mesh(vertices);
        meshes.put(mesh.getVaoID(), mesh);
        return new Model(mesh);
    }
    public static Model loadModel(float[] vertices){
        Mesh mesh = new Mesh(vertices, null, 3);
        meshes.put(mesh.getVaoID(), mesh);
        return new Model(mesh);
    }
    public static Model loadModel(float[] vertices, float[] uvs, int[] triangles, float[] normals){
        Mesh mesh = new Mesh(vertices, uvs, triangles, normals);
        meshes.put(mesh.getVaoID(), mesh);
        return new Model(mesh);
    }

    public static int loadModelID(float[] vertices, float[] uvs, int dimensions){
        Mesh mesh = new Mesh(vertices, uvs, dimensions);
        meshes.put(mesh.getVaoID(), mesh);
        return mesh.getVaoID();
    }

    public static void unloadModel(int modelID){
        meshes.get(modelID).cleanUp();
    }

    public static void cleanUp(){
        for(Mesh mesh: meshes.values()){
            mesh.cleanUp();
        }
        TextureLoader.cleanUp();
    }
}
