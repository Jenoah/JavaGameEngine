package nl.jenoah.core.entity;

import nl.jenoah.core.debugging.Debug;
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

public class Mesh {
    private Vector3f[] vertices;
    private Vector3f[] normals;
    private Vector2f[] uvs;
    private int[] triangles;
    private int dimension = 3;

    private final List<Integer> vbos = new ArrayList<>();

    private int vaoID = -1;
    private int vertexVBOID, normalVBOID, triangleVBOID, uvVBOID = -1;

    private int vertexCount = -1;

    public Mesh(Vector3f[] vertices){
        this.vertices = vertices;

        float[] verticesStripped = Conversion.toFloatArray(vertices);

        load(verticesStripped, null, null, null);
    }
    public Mesh(Vector2f[] vertices){
        Vector3f[] vertices3D = new Vector3f[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            vertices3D[i] = new Vector3f(vertices[i].x, vertices[i].y, 0);
        }
        this.vertices = vertices3D;

        float[] verticesStripped = Conversion.toFloatArray(vertices3D);

        load(verticesStripped, null, null, null);
    }
    public Mesh(Vector3f[] vertices, int[] triangles){
        this.vertices = vertices;
        this.triangles = triangles;

        float[] verticesStripped = Conversion.toFloatArray(vertices);

        load(verticesStripped, null, triangles, null);
    }
    public Mesh(Vector3f[] vertices, Vector2f[] uvs, int[] triangles){
        this.vertices = vertices;
        this.uvs = uvs;
        this.triangles = triangles;

        float[] verticesStripped = Conversion.toFloatArray(vertices);
        float[] uvFloatArray = Conversion.toFloatArray(uvs);

        load(verticesStripped, uvFloatArray, triangles, null);
    }
    public Mesh(Vector3f[] vertices, Vector2f[] uvs, int[] triangles, Vector3f[] normals){
        float[] verticesStripped = Conversion.toFloatArray(vertices);
        float[] uvFloatArray = uvs != null ? Conversion.toFloatArray(uvs) : null;
        float[] normalFloatArray = normals != null ? Conversion.toFloatArray(normals) : null;

        load(verticesStripped, uvFloatArray, triangles, normalFloatArray);
    }

    public Mesh(float[] vertices, float[] uvs, int dimensions){
        this.dimension = dimensions;
        load(vertices, uvs, null, null);
    }
    public Mesh(float[] vertices, float[] uvs, int[] triangles, float[] normals){
        this.triangles = triangles;
        load(vertices, uvs, triangles, normals);
    }

    private void load(float[] vertexFloatArray, float[] uvFloatArray, int[] triangleArray, float[] normals){
        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        if(triangleArray != null) {
            triangleVBOID = StoreIndicesBuffer(triangleArray);
            if(this.triangles == null){
                this.triangles = triangleArray;
            }
        }
        if(vertexFloatArray != null){
            vertexVBOID = storeDataInAttributeList(0, this.dimension, vertexFloatArray);
            if(this.vertices == null) {
                if(this.dimension == 3) this.vertices = Conversion.floatArrayToVector3Array(vertexFloatArray);
            }
            vertexCount = vertexFloatArray.length;
        }

        if(normals != null) {
            normalVBOID = storeDataInAttributeList(2, 3, normals);
            if(this.normals == null){
                this.normals = Conversion.floatArrayToVector3Array(normals);
            }
        }

        if(uvFloatArray != null){
            uvVBOID = storeDataInAttributeList(1, 2, uvFloatArray);
            if(this.uvs == null){
                this.uvs = Conversion.floatArrayToVector2Array(uvFloatArray);
            }
        }

        unbind();
    }

    private int StoreIndicesBuffer(int[] triangles){
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(triangles);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
        return vbo;
    }
    private int storeDataInAttributeList(int attributeNumber, int vertexCount, float[] data){
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public void unbind(){
        GL30.glBindVertexArray(0);
    }

//    Recalculate

    public void calculateNormals(){
        normals = new Vector3f[vertices.length];

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
    }
    //INCOMPLETE FUNCTION OF calculateTangents
    private static void calculateTangents(List<Vector3f> vertices, int[] triangles, Vector2f textureCoords) {
        int triangleCount = triangles.length / 3;

        for (int i = 0; i < triangleCount; i++) {
            int normalTriangleIndex = i * 3;
            int vertexIndexA = triangles[normalTriangleIndex];
            int vertexIndexB = triangles[normalTriangleIndex + 1];
            int vertexIndexC = triangles[normalTriangleIndex + 2];

            Vector3f delta1 = Calculus.subtractVectors(vertices.get(vertexIndexB), vertices.get(vertexIndexA));
            Vector3f delta2 = Calculus.subtractVectors(vertices.get(vertexIndexC), vertices.get(vertexIndexA));
        }
    }
    public void generateUVs(){
        uvs = new Vector2f[triangles.length];
        for (int i = 0; i < triangles.length - 2; i += 3) {
            Vector3f norm = new Vector3f(normals[i]);

            float dotX = Math.abs(norm.dot(new Vector3f(1, 0, 0)));
            float dotY = Math.abs(norm.dot(new Vector3f(0, 1, 0)));
            float dotZ = Math.abs(norm.dot(new Vector3f(0, 0, 1)));

            if (dotX > dotY && dotX > dotZ) {
                for (int j = 0; j < 3; j++) {
                    int triangleIndex = triangles[i + j];
                    uvs[triangleIndex] = new Vector2f(vertices[triangleIndex].z, vertices[triangleIndex].y);
                }
            } else {
                if (dotY > dotX && dotY > dotZ) {
                    for (int j = 0; j < 3; j++) {
                        int triangleIndex = triangles[i + j];
                        uvs[triangleIndex] = new Vector2f(vertices[triangleIndex].x, vertices[triangleIndex].z);
                    }
                } else {
                    for (int j = 0; j < 3; j++) {
                        int triangleIndex = triangles[i + j];
                        uvs[triangleIndex] = new Vector2f(vertices[triangleIndex].x, vertices[triangleIndex].y);
                    }
                }
            }
        }

        float[] uvFloatArray = Conversion.toFloatArray(uvs);
        if(uvVBOID == -1){
            GL30.glBindVertexArray(vaoID);
            uvVBOID = storeDataInAttributeList(1, 2, uvFloatArray);
            unbind();
        }else {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvVBOID);
            FloatBuffer buffer = Utils.storeDataInFloatBuffer(uvFloatArray);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(1, vertexCount, GL11.GL_FLOAT, false, 0, 0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }
    }

//    Setters

    public void setUvs(Vector2f[] uvs){
        this.uvs = uvs;
    }
    public void setUVScale(float uvScale){
        if(uvVBOID == -1){
            Debug.Log("Texture coordinates not set");
            return;
        }

        float[] updatedUVs = Conversion.toFloatArray(uvs);
        for (int i = 0; i < updatedUVs.length; i++) {
            updatedUVs[i] *= uvScale;
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvVBOID);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(updatedUVs);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        this.uvs = Conversion.floatArrayToVector2Array(updatedUVs);
    }


    public void setNormals(Vector3f[] normals){
        this.normals = normals;
    }
    public void setNormals(Vector2f[] normals){
        this.normals = new Vector3f[normals.length];
        for (int i = 0; i < normals.length; i++) {
            this.normals[i] = new Vector3f(normals[i].x, normals[i].y, 0);
        }
    }

//     Getters

    public final int getVertexCount(){
        if(vertexCount == -1){
            vertexCount = vertices.length;
        }

        return vertexCount;
    }

    public final int getVaoID(){
        return vaoID;
    }
    public final int[] getTriangles() {
        return triangles;
    }
    public final Vector2f[] getUVs() {
        return uvs;
    }
    public final Vector3f[] getNormals() {
        return normals;
    }
    public final Vector3f[] getVertices() {
        return vertices;
    }


    public void cleanUp(){
        GL30.glDeleteVertexArrays(vaoID);
        for(int vbo: vbos){
            GL30.glDeleteBuffers(vbo);
        }
    }
}
