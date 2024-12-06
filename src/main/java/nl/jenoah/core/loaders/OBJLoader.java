package nl.jenoah.core.loaders;

import nl.jenoah.core.ModelManager;
import nl.jenoah.core.entity.Model;
import nl.jenoah.core.rendering.Face;
import nl.jenoah.core.utils.Conversion;
import nl.jenoah.core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class OBJLoader {

    private ModelManager modelManager;

    public OBJLoader(){
        this.modelManager = ModelManager.getInstance();
    }

    public Model loadOBJModel(String fileName) {
        List<String> lines = Utils.readAllLines(fileName);
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
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


        return loadModel(Conversion.v3ToFloatArray(finalVertices), Conversion.v2ToFloatArray(finalTextures), Conversion.v3ToFloatArray(finalNormals), indicesArray);
    }

    /*

    public Model loadOBJModel(String fileName){
        List<String> lines = Utils.readAllLines(fileName);
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();

        for (String line : lines){
            String[] tokens = line.split("\\s+");
            switch (tokens[0]){
                case "v":
                    //Vertices
                    Vector3f vertex = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(vertex);
                    break;
                case "vt":
                    //Vertex textures
                    Vector2f texture = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textures.add(texture);
                    break;
                case "vn":
                    //Vertex normals
                    Vector3f normal = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normals.add(normal);
                    break;
                case "f":
                    //Faces
                    for(int i=1; i< tokens.length; i++){
                        processFace(tokens[i],faces);
                    }
                    break;
                default:
                    break;
            }
        }

        List<Integer> indices = new ArrayList<>();
        float[] strippedVertices = new float[vertices.size() * 3];
        float[] strippedTextureCoords = new float[vertices.size() * 2];
        float[] strippedNormals = new float[vertices.size() * 3];

        int i = 0;
        for(Vector3f position : vertices){
            strippedVertices[i * 3] = position.x;
            strippedVertices[i * 3 + 1] = position.y;
            strippedVertices[i * 3 + 2] = position.z;
            i++;
        }

        for(Vector3i face : faces){
            processVertex(face.x, face.y, face.z, textures, normals, indices, strippedTextureCoords, strippedNormals);
        }

        int[] strippedIndices = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(strippedVertices, strippedTextureCoords, strippedNormals, strippedIndices);

    }

    private static void processVertex(int position, int textureCoord, int normal, List<Vector2f> textureCoordList,
                                      List<Vector3f> normalList, List<Integer> indicesList,
                                      float[] textureCoordArray, float[] normalArray){

        indicesList.add(position);

        if(textureCoord >= 0){
            Vector2f textureCoordVector = textureCoordList.get(textureCoord);
            textureCoordArray[position * 2] = textureCoordVector.x;
            textureCoordArray[position * 2 + 1] = 1 - textureCoordVector.y;
        }

        if(normal >= 0){
            Vector3f normalVector = normalList.get(normal);
            normalArray[position * 3] = normalVector.x;
            normalArray[position * 3 + 1] = normalVector.y;
            normalArray[position * 3 + 2] = normalVector.z;
        }
    }

    private static void processFace(String token, List<Vector3i> faces){
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int position = -1;
        int textureCoord = -1;
        int normal = -1;

        position = Integer.parseInt(lineToken[0]) - 1;
        if(length > 1){
            String textureCoordinate = lineToken[1];
            textureCoord = textureCoordinate.length() > 0 ? Integer.parseInt(textureCoordinate) - 1 : -1;
            if(length > 2){
                normal = Integer.parseInt(lineToken[2]) - 1;
            }
        }

        Vector3i face = new Vector3i(position, textureCoord, normal);
        faces.add(face);
    }

    /**/

    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices){
        int id = modelManager.createVAO();

        modelManager.StoreIndicesBuffer(indices);
        modelManager.storeDataInAttributeList(0, 3, vertices);
        modelManager.storeDataInAttributeList(1, 2, textureCoords);
        modelManager.storeDataInAttributeList(2, 3, normals);
        modelManager.unbind();
        return new Model(id, indices.length);
    }

    public Model loadModel(float[] vertices, float[] textureCoords, int[] indices){
        int id = modelManager.createVAO();

        modelManager.StoreIndicesBuffer(indices);
        modelManager.storeDataInAttributeList(0, 3, vertices);
        modelManager.storeDataInAttributeList(1, 2, textureCoords);
        modelManager.unbind();
        return new Model(id, indices.length);
    }

    public Model loadModel(Vector3f[] vertices, Vector2f[] textureCoords, List<Integer> indices){
        float[] verticesStripped = Conversion.v3ToFloatArray(vertices);
        float[] textureCoordsStripped = Conversion.v2ToFloatArray(textureCoords);
        int[] indicesStripped = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesStripped, textureCoordsStripped, indicesStripped);
    }

    public Model loadModel(Vector3f[] vertices, Vector2f[] textureCoords, float[] normals, List<Integer> indices){
        float[] verticesStripped = Conversion.v3ToFloatArray(vertices);
        float[] textureCoordsStripped = Conversion.v2ToFloatArray(textureCoords);
        int[] indicesStripped = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesStripped, textureCoordsStripped, normals, indicesStripped);
    }



}
