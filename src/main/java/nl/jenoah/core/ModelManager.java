package nl.jenoah.core;

import nl.jenoah.core.entity.Model;
import nl.jenoah.core.loaders.*;
import nl.jenoah.core.utils.Calculus;
import nl.jenoah.core.utils.Conversion;
import nl.jenoah.core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelManager {
    private static final List<Integer> vaos = new ArrayList<>();
    private static final List<Integer> vbos = new ArrayList<>();

    public static Model loadModel(float[] vertices, float[] textureCoords, int[] indices){
        int id = createVAO();

        StoreIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        int textureCoordsVBOID = storeDataInAttributeList(1, 2, textureCoords);
        unbind();

        Model model = new Model(id, indices.length);
        model.setTextureCoordinates(textureCoordsVBOID, textureCoords);
        return model;
    }

    //LWJGL Vectors
    public static Model loadModel(Vector3f[] vertices, Vector2f[] textureCoords, List<Integer> indices){
        float[] verticesStripped = Conversion.v3ToFloatArray(vertices);
        float[] textureCoordsStripped = Conversion.v2ToFloatArray(textureCoords);
        int[] indicesStripped = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesStripped, textureCoordsStripped, indicesStripped);
    }

    //LWJLG Vectors + normals
    public static Model loadModel(Vector3f[] vertices, Vector2f[] textureCoords, float[] normals, List<Integer> indices){
        float[] verticesStripped = Conversion.v3ToFloatArray(vertices);
        float[] textureCoordsStripped = Conversion.v2ToFloatArray(textureCoords);
        int[] indicesStripped = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesStripped, textureCoordsStripped, normals, indicesStripped);
    }

    public static Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices){
        int id = createVAO();

        StoreIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        int textureCoordsVBOID = storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbind();

        Model model = new Model(id, indices.length);
        model.setTextureCoordinates(textureCoordsVBOID, textureCoords);
        return model;
    }

    public static Model loadModel(float[] vertices, int[] indices){
        int id = createVAO();

        StoreIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        unbind();

        return new Model(id, indices.length);
    }

    public static Model loadModel(float[] position){
        int id = ModelManager.createVAO();

        ModelManager.storeDataInAttributeList(0, 2, position);

        ModelManager.unbind();
        return new Model(id, position.length);
    }

    public static Model loadModel(float[] vertices, int dimensions){
        int id = ModelManager.createVAO();
        ModelManager.storeDataInAttributeList(0, dimensions, vertices);
        ModelManager.unbind();
        return new Model(id, vertices.length / dimensions);
    }

    public static int loadModelID(float[] positions, float[] textureCoords){
        int id = ModelManager.createVAO();

        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 2, textureCoords);

        ModelManager.unbind();
        return id;
    }

    public static int createVAO(){
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    public static void unloadModel(int modelID){
        GL30.glDeleteVertexArrays(modelID);
        vaos.remove((Integer)modelID);
        //TODO: Delete buffers / VBO of model
    }

    public static void StoreIndicesBuffer(int[] indices){
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
    }

    public static int storeDataInAttributeList(int attributeNumber, int vertexCount, float[] data){
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public static void unbind(){
        GL30.glBindVertexArray(0);
    }

    public static void cleanUp(){
        for(int vao: vaos){
            GL30.glDeleteVertexArrays(vao);
        }for(int vbo: vbos){
            GL30.glDeleteBuffers(vbo);
        }
        TextureLoader.cleanUp();
    }

    public static Vector3f[] calculateNormals(Vector3f[] vertices, int[] triangles){
        Vector3f[] normals = new Vector3f[vertices.length];

        int triangleCount = triangles.length / 3;

        for(int i = 0; i < triangleCount; i++){
            int normalTriangleIndex = i * 3;
            int vertexIndexA = triangles[normalTriangleIndex];
            int vertexIndexB = triangles[normalTriangleIndex + 1];
            int vertexIndexC = triangles[normalTriangleIndex + 2];

            Vector3f CB = Calculus.subtractVectors(vertices[vertexIndexB], vertices[vertexIndexA]);
            Vector3f CA = Calculus.subtractVectors(vertices[vertexIndexC], vertices[vertexIndexA]);

            Vector3f triangleNormal = CB.cross(CA).normalize();

            normals[vertexIndexA] = triangleNormal;
            normals[vertexIndexB] = triangleNormal;
            normals[vertexIndexC] = triangleNormal;
        }

        return normals;
    }

    public static Vector3f[] calculateNormals(List<Vector3f> vertices, List<Integer> triangles){
        return calculateNormals(vertices.toArray(new Vector3f[0]), triangles.stream().mapToInt(i->i).toArray());
    }

    public static Vector2f[] generateUVs(List<Vector3f> vertices, List<Integer> triangles, Vector3f[] normals){
        Vector2f[] uvs = new Vector2f[triangles.size()];
        for (int i = 0; i < triangles.size() - 2; i += 3) {
            Vector3f norm = new Vector3f(normals[i]);

            float dotX = Math.abs(norm.dot(new Vector3f(1, 0, 0)));
            float dotY = Math.abs(norm.dot(new Vector3f(0, 1, 0)));
            float dotZ = Math.abs(norm.dot(new Vector3f(0, 0, 1)));

            if (dotX > dotY && dotX > dotZ) {
                for (int j = 0; j < 3; j++) {
                    int triangleIndex = triangles.get(i + j);
                    uvs[triangleIndex] = new Vector2f(vertices.get(triangleIndex).z, vertices.get(triangleIndex).y);
                }
            } else {
                if (dotY > dotX && dotY > dotZ) {
                    for (int j = 0; j < 3; j++) {
                        int triangleIndex = triangles.get(i + j);
                        uvs[triangleIndex] = new Vector2f(vertices.get(triangleIndex).x, vertices.get(triangleIndex).z);
                    }
                } else {
                    for (int j = 0; j < 3; j++) {
                        int triangleIndex = triangles.get(i + j);
                        uvs[triangleIndex] = new Vector2f(vertices.get(triangleIndex).x, vertices.get(triangleIndex).y);
                    }
                }
            }
        }

        return uvs;
    }
}
