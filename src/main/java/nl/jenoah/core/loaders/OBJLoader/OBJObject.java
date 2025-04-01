package nl.jenoah.core.loaders.OBJLoader;

import nl.jenoah.core.rendering.Face;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class OBJObject {
    private ArrayList<OBJModel> objModels = new ArrayList<>();
    List<Vector3f> vertices = new ArrayList<>();
    List<Vector3f> normals = new ArrayList<>();
    List<Vector2f> textures = new ArrayList<>();
    List<Integer> triangles = new ArrayList<>();

    public ArrayList<OBJModel> getObjModels() {
        return objModels;
    }

    public void setObjModels(ArrayList<OBJModel> objModels) {
        this.objModels = objModels;
    }

    public void addObjModel(OBJModel objModel){
        this.objModels.add(objModel);
    }

    public List<Vector3f> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vector3f> vertices) {
        this.vertices = vertices;
    }

    public List<Vector3f> getNormals() {
        return normals;
    }

    public void setNormals(List<Vector3f> normals) {
        this.normals = normals;
    }

    public List<Vector2f> getTextures() {
        return textures;
    }

    public void setTextures(List<Vector2f> textures) {
        this.textures = textures;
    }

    public List<Integer> getTriangles() {
        return triangles;
    }

    public void setTriangles(List<Integer> triangles) {
        this.triangles = triangles;
    }

    public void cleanUp(){
        List<Integer> indicesToRemove = new ArrayList<>();

        for (int i = 0; i < objModels.size(); i++) {
            if(objModels.get(i).getFaces().isEmpty()) indicesToRemove.add(i);
        }

        for (Integer integer : indicesToRemove) {
            objModels.remove((int) integer);

        }
    }
}
