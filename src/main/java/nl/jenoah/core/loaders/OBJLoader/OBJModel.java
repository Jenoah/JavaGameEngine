package nl.jenoah.core.loaders.OBJLoader;

import nl.jenoah.core.entity.Material;
import nl.jenoah.core.rendering.Face;
import nl.jenoah.core.utils.Conversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class OBJModel {
    private ArrayList<Face> faces = new ArrayList<>();
    private int[] indices = new int[0];
    private Material material;
    private String name = "Undefined name";

    public OBJModel(){}

    public OBJModel(ArrayList<Face> faces) {
        this.faces = faces;
        ArrayList<Integer> faceIndices = new ArrayList<>();
        faces.forEach(face -> {
            faceIndices.addAll(Arrays.stream(face.getVertices()).boxed().toList());
        });
        indices = Conversion.ToIntArray(faceIndices);
    }

    public OBJModel(ArrayList<Face> faces, Material material) {
        this.faces = faces;
        ArrayList<Integer> faceIndices = new ArrayList<>();
        faces.forEach(face -> {
            faceIndices.addAll(Arrays.stream(face.getVertices()).boxed().toList());
        });
        indices = Conversion.ToIntArray(faceIndices);
        this.material = material;
    }

    public OBJModel(Material material) {
        this.material = material;
    }

    public OBJModel(String name) {
        this.name = name;
    }

    public ArrayList<Face> getFaces() {
        return faces;
    }

    public void addFace(Face face){
        this.faces.add(face);
    }

    public int[] getIndices() {
        if(indices.length == 0 && !faces.isEmpty()){
            ArrayList<Integer> faceIndices = new ArrayList<>();
            faces.forEach(face -> {
                faceIndices.addAll(Arrays.stream(face.getVertices()).boxed().toList());
            });
            indices = Conversion.ToIntArray(faceIndices);
        }
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void cleanUp(){

    }
}
