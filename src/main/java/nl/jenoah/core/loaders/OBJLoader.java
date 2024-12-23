package nl.jenoah.core.loaders;

import nl.jenoah.core.ModelManager;
import nl.jenoah.core.entity.Model;
import nl.jenoah.core.entity.Texture;
import nl.jenoah.core.rendering.Face;
import nl.jenoah.core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class OBJLoader {


    public static Model loadOBJModel(String fileName) {
        List<String> lines = Utils.readAllLines(fileName);
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Integer> triangles = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        List<Vector3f> finalVertices = new ArrayList<>();
        List<Vector2f> finalTextures = new ArrayList<>();
        List<Vector3f> finalNormals = new ArrayList<>();
        List<Integer> finalIndices = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
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
                    int[] vertexIndices = new int[3];
                    int[] textureIndices = new int[3];
                    int[] normalIndices = new int[3];

                    for (int i = 1; i <= 3; i++) {
                        String[] vertexData = tokens[i].split("/");
                        vertexIndices[i - 1] = Integer.parseInt(vertexData[0]) - 1; // Vertex index

                        if (vertexData.length > 1 && !vertexData[1].isEmpty()) {
                            textureIndices[i - 1] = Integer.parseInt(vertexData[1]) - 1; // Texture index
                        } else {
                            textureIndices[i - 1] = -1; // Default or invalid index if not present
                        }

                        if (vertexData.length > 2 && !vertexData[2].isEmpty()) {
                            normalIndices[i - 1] = Integer.parseInt(vertexData[2]) - 1; // Normal index
                        } else {
                            normalIndices[i - 1] = -1; // Default or invalid index if not present
                        }
                    }

                    faces.add(new Face(vertexIndices, textureIndices, normalIndices));
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

        int[] indicesArray = finalIndices.stream().mapToInt((Integer v) -> v).toArray();


        return ModelManager.loadModel(finalVertices.toArray(new Vector3f[0]), finalTextures.toArray(new Vector2f[0]), indicesArray, finalNormals.toArray(new Vector3f[0]));
    }

    public static Model loadOBJModel(String fileName, Texture texturePath) {
        Model model = loadOBJModel(fileName);
        model.getMaterial().setAlbedoTexture(texturePath);

        return model;
    }



}
