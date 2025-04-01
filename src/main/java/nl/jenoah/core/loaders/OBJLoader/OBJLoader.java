package nl.jenoah.core.loaders.OBJLoader;

import nl.jenoah.core.ModelManager;
import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.Material;
import nl.jenoah.core.entity.Model;
import nl.jenoah.core.entity.Texture;
import nl.jenoah.core.rendering.Face;
import nl.jenoah.core.shaders.ShaderManager;
import nl.jenoah.core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OBJLoader {

    public static Model loadOBJModel(String fileName) {
        List<String> lines = Utils.readAllLines(fileName);
        OBJObject objObject = new OBJObject();

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Integer> triangles = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        List<Vector3f> finalVertices = new ArrayList<>();
        List<Vector2f> finalTextures = new ArrayList<>();
        List<Vector3f> finalNormals = new ArrayList<>();
        List<Integer> finalIndices = new ArrayList<>();

        OBJModel currentModel = new OBJModel();
        String objName = "";

        Random rand = new Random();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "o":
                    objName = tokens[1];
                    currentModel = new OBJModel(objName);
                    objObject.addObjModel(currentModel);
                    Debug.Log("OBJ Object name: " + currentModel.getName());
                    break;
                case "mtllib":
                    Debug.Log("MTL filename: " + tokens[1]);
                    break;
                case "usemtl":
                    Vector4f matColor = new Vector4f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1);
                    currentModel = new OBJModel(objName + " - " + tokens[1]);
                    currentModel.setMaterial(new Material(ShaderManager.pbrShader).setAmbientColor(matColor));
                    objObject.addObjModel(currentModel);
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
                    // Handle faces, whether it's a triangle or quad
                    int faceCount = tokens.length - 1; // First token is 'f', so we subtract 1
                    int[] vertexIndices = new int[faceCount];
                    int[] textureIndices = new int[faceCount];
                    int[] normalIndices = new int[faceCount];

                    for (int i = 1; i <= faceCount; i++) {
                        String[] vertexData = tokens[i].split("/");

                        // Get the vertex index
                        vertexIndices[i - 1] = Integer.parseInt(vertexData[0]) - 1;

                        // Check if there's texture data and set texture index
                        textureIndices[i - 1] = vertexData.length > 1 && !vertexData[1].isEmpty()
                                ? Integer.parseInt(vertexData[1]) - 1 : -1;

                        // Check if there's normal data and set normal index
                        normalIndices[i - 1] = vertexData.length > 2 && !vertexData[2].isEmpty()
                                ? Integer.parseInt(vertexData[2]) - 1 : -1;
                    }

                    // If it's a quad (4 vertices), split into 2 triangles
                    if (faceCount == 4) {
                        // First triangle: vertices 1, 2, 3
                        int[] vertexIndices1 = {vertexIndices[0], vertexIndices[1], vertexIndices[2]};
                        int[] textureIndices1 = {textureIndices[0], textureIndices[1], textureIndices[2]};
                        int[] normalIndices1 = {normalIndices[0], normalIndices[1], normalIndices[2]};

                        // Second triangle: vertices 1, 3, 4
                        int[] vertexIndices2 = {vertexIndices[0], vertexIndices[2], vertexIndices[3]};
                        int[] textureIndices2 = {textureIndices[0], textureIndices[2], textureIndices[3]};
                        int[] normalIndices2 = {normalIndices[0], normalIndices[2], normalIndices[3]};

                        // Create and add faces
                        Face face1 = new Face(vertexIndices1, textureIndices1, normalIndices1);
                        Face face2 = new Face(vertexIndices2, textureIndices2, normalIndices2);

                        faces.add(face1);
                        faces.add(face2);
                        currentModel.addFace(face1);
                        currentModel.addFace(face2);
                    } else {
                        // For triangles (3 vertices), just create one face
                        Face face = new Face(vertexIndices, textureIndices, normalIndices);
                        faces.add(face);
                        currentModel.addFace(face);
                    }
                    break;
            }
        }

        for (Face face : faces) {
            for (int i = 0; i < face.getVertices().length; i++) {
                int vertexIndex = face.getVertices()[i];
                finalVertices.add(vertices.get(vertexIndex));

                int textureIndex = face.getTextureCoords()[i];
                if (textureIndex != -1) { // Only add if valid
                    finalTextures.add(textures.get(textureIndex));
                }

                int normalIndex = face.getNormals()[i];
                if (normalIndex != -1) { // Only add if valid
                    finalNormals.add(normals.get(normalIndex));
                }

                finalIndices.add(finalIndices.size()); // Use the current index in the final vertex list
            }
        }

        objObject.setVertices(finalVertices);
        objObject.setTextures(finalTextures);
        objObject.setTriangles(finalIndices);
        objObject.setNormals(finalNormals);
        objObject.cleanUp();

        //TODO: MAKE ALL OF THESE 'OBJObjects' CONVERT TO MESHES AND RENDER THEM SEPARATELY?
        objObject.getObjModels().forEach((objModel -> { Debug.Log("Got " + objModel.getName() + " with face count of " + objModel.getFaces().size());}));

        return ModelManager.loadModel(objObject);}

    public static Model loadOBJModel(String fileName, Texture texturePath) {
        Model model = loadOBJModel(fileName);
        model.getMaterial().setAlbedoTexture(texturePath);

        return model;
    }

}
