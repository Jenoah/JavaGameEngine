package nl.jenoah.core.loaders.OBJLoader;

import nl.jenoah.core.ModelManager;
import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.Material;
import nl.jenoah.core.entity.Model;
import nl.jenoah.core.entity.Texture;
import nl.jenoah.core.loaders.TextureLoader;
import nl.jenoah.core.rendering.Face;
import nl.jenoah.core.rendering.MeshMaterialSet;
import nl.jenoah.core.shaders.ShaderManager;
import nl.jenoah.core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class OBJLoader {

    public static List<MeshMaterialSet> loadOBJModel(String fileName) {
        List<String> lines = Utils.readAllLines(fileName);
        OBJObject objObject = new OBJObject();

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();

        OBJModel currentModel = null;
        String objName = "";
        Random rand = new Random();
        HashMap<String, Material> mtlInfo = new HashMap<>();
        String mtlFolder = Paths.get(fileName).getParent().toString() + "/";

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "o":
                    objName = tokens[1];
                    currentModel = new OBJModel(objName);
                    currentModel.setMaterial(new Material(ShaderManager.pbrShader));
                    objObject.addObjModel(currentModel);
                    Debug.Log("OBJ Object name: " + currentModel.getName());
                    break;
                case "mtllib":
                    Debug.Log("MTL filename: " + mtlFolder + tokens[1]);
                    mtlInfo = loadMTL(mtlFolder + tokens[1]);
                    break;
                case "usemtl":
                    currentModel = new OBJModel(objName + " - " + tokens[1]);
                    if(mtlInfo.containsKey(tokens[1])){
                        currentModel.setMaterial(mtlInfo.get(tokens[1]));
                    }else{
                        currentModel.setMaterial(new Material(ShaderManager.pbrShader));
                    }
                    objObject.addObjModel(currentModel);
                    Debug.Log("Using MTL " + tokens[1]);
                    break;
                case "v":
                    vertices.add(new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    ));
                    break;
                case "vn":
                    normals.add(new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    ));
                    break;
                case "vt":
                    textures.add(new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    ));
                    break;
                case "f":
                    if (currentModel == null) {
                        // If no object/material has been defined yet, create a default one
                        currentModel = new OBJModel("default");
                        currentModel.setMaterial(new Material(ShaderManager.pbrShader));
                        objObject.addObjModel(currentModel);
                    }

                    int faceCount = tokens.length - 1;
                    int[] vertexIndices = new int[faceCount];
                    int[] textureIndices = new int[faceCount];
                    int[] normalIndices = new int[faceCount];

                    for (int i = 1; i <= faceCount; i++) {
                        String[] vertexData = tokens[i].split("/");

                        vertexIndices[i - 1] = Integer.parseInt(vertexData[0]) - 1;
                        textureIndices[i - 1] = vertexData.length > 1 && !vertexData[1].isEmpty()
                                ? Integer.parseInt(vertexData[1]) - 1 : -1;
                        normalIndices[i - 1] = vertexData.length > 2 && !vertexData[2].isEmpty()
                                ? Integer.parseInt(vertexData[2]) - 1 : -1;
                    }

                    if (faceCount == 4) {
                        Face face1 = new Face(
                                new int[]{vertexIndices[0], vertexIndices[1], vertexIndices[2]},
                                new int[]{textureIndices[0], textureIndices[1], textureIndices[2]},
                                new int[]{normalIndices[0], normalIndices[1], normalIndices[2]}
                        );
                        Face face2 = new Face(
                                new int[]{vertexIndices[0], vertexIndices[2], vertexIndices[3]},
                                new int[]{textureIndices[0], textureIndices[2], textureIndices[3]},
                                new int[]{normalIndices[0], normalIndices[2], normalIndices[3]}
                        );
                        currentModel.addFace(face1);
                        currentModel.addFace(face2);
                    } else {
                        Face face = new Face(vertexIndices, textureIndices, normalIndices);
                        currentModel.addFace(face);
                    }
                    break;
            }
        }

        // If still no model was created, create a default one
        if (objObject.getObjModels().isEmpty()) {
            currentModel = new OBJModel("default");
            currentModel.setMaterial(new Material(ShaderManager.pbrShader));
            objObject.addObjModel(currentModel);
        }

        // Now, for each OBJModel, build its own vertex/index lists
        for (OBJModel model : objObject.getObjModels()) {
            List<Vector3f> finalVertices = new ArrayList<>();
            List<Vector2f> finalTextures = new ArrayList<>();
            List<Vector3f> finalNormals = new ArrayList<>();
            List<Integer> finalIndices = new ArrayList<>();
            Map<String, Integer> uniqueVertexMap = new HashMap<>();

            for (Face face : model.getFaces()) {
                for (int i = 0; i < face.getVertexIndices().length; i++) {
                    int vIdx = face.getVertexIndices()[i];
                    int tIdx = face.getTextureCoords()[i];
                    int nIdx = face.getNormals()[i];

                    // Build a unique key for this vertex/tex/normal combination
                    String key = vIdx + "/" + tIdx + "/" + nIdx;
                    Integer index = uniqueVertexMap.get(key);
                    if (index == null) {
                        finalVertices.add(vertices.get(vIdx));
                        if (tIdx != -1) finalTextures.add(textures.get(tIdx));
                        if (nIdx != -1) finalNormals.add(normals.get(nIdx));
                        index = finalVertices.size() - 1;
                        uniqueVertexMap.put(key, index);
                    }
                    finalIndices.add(index);
                }
            }

            model.setVertices(finalVertices);
            model.setUvs(finalTextures);
            model.setNormals(finalNormals);
            model.setIndices(finalIndices);
        }

        //objObject.cleanUp();

        return ModelManager.loadModel(objObject);
    }

    public static List<MeshMaterialSet> loadOBJModel(String fileName, Texture texturePath) {
        Debug.Log("Loading OBJ " + fileName);
        List<MeshMaterialSet> meshMaterialSets = loadOBJModel(fileName);
        meshMaterialSets.forEach((meshMaterialSet -> {
            meshMaterialSet.material.setAlbedoTexture(texturePath);
        }));

        return meshMaterialSets;
    }

    public static HashMap<String, Material> loadMTL(String fileName) {
        HashMap<String, Material> mtlMaterials = new HashMap<>();
        Material currentMaterial = null;

        List<String> lines = Utils.readAllLines(fileName);
        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "newmtl":
                    currentMaterial = new Material(ShaderManager.pbrShader);
                    mtlMaterials.put(tokens[1], currentMaterial);
                    break;
                case "Kd":
                    currentMaterial.setDiffuseColor(new Vector4f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]), 1));
                    break;
                case "Ks":
                    currentMaterial.setSpecularColor(new Vector4f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]), 1));
                    break;
                case "Ka":
                    currentMaterial.setAmbientColor(new Vector4f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]), 1));
                    break;
                case "map_Kd":
                    StringBuilder texturePath = new StringBuilder();
                    for(int i = 1; i < tokens.length; i++){
                        texturePath.append(tokens[i]);
                    }
                        texturePath = new StringBuilder("textures/" + Paths.get(texturePath.toString()).getFileName().toString());
                    try {
                        Texture texture = new Texture(TextureLoader.loadTexture(texturePath.toString()));
                        currentMaterial.setAlbedoTexture(texture);
                    } catch (Exception e) {
                        Debug.LogError("Texture is not placed in resource texture folder: " + texturePath);
                    }
                    break;
            }
        }
        return mtlMaterials;
    }

}
