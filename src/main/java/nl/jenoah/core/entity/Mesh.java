package nl.jenoah.core.entity;

import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.utils.Calculus;
import nl.jenoah.core.utils.Conversion;
import nl.jenoah.core.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private Vector3f[] vertices;
    private Vector3f[] normals;
    private Vector3f[] tangents;
    private Vector3f[] bitangents;
    private Vector2f[] uvs;
    private int[] triangles;
    private int dimension = 3;

    private final List<Integer> vbos = new ArrayList<>();

    private int vaoID = -1;
    private int vertexVBOID = -1, normalVBOID = -1, tangentsVBOID = -1, bitangentsVBOID = -1, triangleVBOID = -1, uvVBOID = -1, instanceVBOID = -1;

    private int vertexCount = -1;

    private boolean isVisible = true;

    //Instancing
    private boolean isInstanced = false;
    private final List<Matrix4f> instanceOffsets = new ArrayList<>();

    public Mesh(Mesh mesh){
        float[] verticesStripped = Conversion.toFloatArray(mesh.vertices);
        float[] uvFloatArray = mesh.uvs != null ? Conversion.toFloatArray(mesh.uvs) : null;
        float[] normalFloatArray = mesh.normals != null ? Conversion.toFloatArray(mesh.normals) : null;

        load(verticesStripped, uvFloatArray, mesh.triangles, normalFloatArray);
    }

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

    public Mesh(Vector3f[] vertices, Vector3f[] normals, int[] triangles){
        this.vertices = vertices;
        this.normals = normals;
        this.triangles = triangles;

        float[] verticesStripped = Conversion.toFloatArray(vertices);
        float[] normalsFloatArray = Conversion.toFloatArray(normals);

        load(verticesStripped, normalsFloatArray, triangles, null);
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

        if(uvFloatArray != null){
            uvVBOID = storeDataInAttributeList(1, 2, uvFloatArray);
            if(this.uvs == null){
                this.uvs = Conversion.floatArrayToVector2Array(uvFloatArray);
            }
        }

        if(normals != null) {
            normalVBOID = storeDataInAttributeList(2, 3, normals);
            if(this.normals == null){
                this.normals = Conversion.floatArrayToVector3Array(normals);
            }
        }


        if(uvs != null && uvs.length > 0 && triangles != null && vertices != null){
            calculateTangents();
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

    public Mesh calculateNormals(){
        int triangleCount = triangles.length / 3;
        normals = new Vector3f[vertices.length];
        for (int i = 0; i < normals.length; i++) {
            normals[i] = new Vector3f(0, 0, 0);
        }

        for (int i = 0; i < triangleCount; i++) {
            int normalTriangleIndex = i * 3;
            int vertexIndexA = triangles[normalTriangleIndex];
            int vertexIndexB = triangles[normalTriangleIndex + 1];
            int vertexIndexC = triangles[normalTriangleIndex + 2];

            Vector3f CB = Calculus.subtractVectors(vertices[vertexIndexB], vertices[vertexIndexA]);
            Vector3f CA = Calculus.subtractVectors(vertices[vertexIndexC], vertices[vertexIndexA]);
            Vector3f triangleNormal = CB.cross(CA).normalize();

            normals[vertexIndexA].add(triangleNormal);
            normals[vertexIndexB].add(triangleNormal);
            normals[vertexIndexC].add(triangleNormal);
        }

        for (Vector3f normal : normals) {
            normal.normalize();
        }

        return this;
    }

    public Mesh calculateTangents() {
        // Initialize tangent and bitangent arrays
        Vector3f[] tangents = new Vector3f[vertices.length];
        Vector3f[] bitangents = new Vector3f[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            tangents[i] = new Vector3f(0, 0, 0);
            bitangents[i] = new Vector3f(0, 0, 0);
        }

        int triangleCount = triangles.length / 3;

        for (int i = 0; i < triangleCount; i++) {
            int i0 = triangles[i * 3];
            int i1 = triangles[i * 3 + 1];
            int i2 = triangles[i * 3 + 2];

            Vector3f v0 = vertices[i0];
            Vector3f v1 = vertices[i1];
            Vector3f v2 = vertices[i2];

            Vector2f uv0 = uvs[i0];
            Vector2f uv1 = uvs[i1];
            Vector2f uv2 = uvs[i2];

            Vector3f deltaPos1 = Calculus.subtractVectors(v1, v0);
            Vector3f deltaPos2 = Calculus.subtractVectors(v2, v0);

            Vector2f deltaUV1 = Calculus.subtractVectors(uv1, uv0);
            Vector2f deltaUV2 = Calculus.subtractVectors(uv2, uv0);

            float r = (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
            if (r == 0.0f) r = 1.0f; // Prevent division by zero

            float f = 1.0f / r;

            Vector3f tangent = new Vector3f(
                    f * (deltaUV2.y * deltaPos1.x - deltaUV1.y * deltaPos2.x),
                    f * (deltaUV2.y * deltaPos1.y - deltaUV1.y * deltaPos2.y),
                    f * (deltaUV2.y * deltaPos1.z - deltaUV1.y * deltaPos2.z)
            );

            Vector3f bitangent = new Vector3f(
                    f * (-deltaUV2.x * deltaPos1.x + deltaUV1.x * deltaPos2.x),
                    f * (-deltaUV2.x * deltaPos1.y + deltaUV1.x * deltaPos2.y),
                    f * (-deltaUV2.x * deltaPos1.z + deltaUV1.x * deltaPos2.z)
            );

            tangents[i0] = tangents[i0].add(tangent);
            tangents[i1] = tangents[i1].add(tangent);
            tangents[i2] = tangents[i2].add(tangent);

            bitangents[i0] = bitangents[i0].add(bitangent);
            bitangents[i1] = bitangents[i1].add(bitangent);
            bitangents[i2] = bitangents[i2].add(bitangent);
        }

        // Normalize tangents and bitangents
        for (int i = 0; i < vertices.length; i++) {
            tangents[i] = tangents[i].normalize();
            bitangents[i] = bitangents[i].normalize();
        }

        // Store or return as needed
        this.tangents = tangents;
        this.bitangents = bitangents;

        float[] tangentFloatArray = Conversion.toFloatArray(tangents);
        float[] bitangentFloatArray = Conversion.toFloatArray(bitangents);

// Tangents
        if (tangentsVBOID == -1) {
            GL30.glBindVertexArray(vaoID);
            tangentsVBOID = storeDataInAttributeList(3, 3, tangentFloatArray);
            unbind();
        } else {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tangentsVBOID);
            FloatBuffer buffer = Utils.storeDataInFloatBuffer(tangentFloatArray);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(3, 3, GL11.GL_FLOAT, false, 0, 0); // location 3, size 3
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }

// Bitangents
        if (bitangentsVBOID == -1) {
            GL30.glBindVertexArray(vaoID);
            bitangentsVBOID = storeDataInAttributeList(4, 3, bitangentFloatArray); // location 4
            unbind();
        } else {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bitangentsVBOID);
            FloatBuffer buffer = Utils.storeDataInFloatBuffer(bitangentFloatArray);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(4, 3, GL11.GL_FLOAT, false, 0, 0); // location 4, size 3
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }


        return this;
    }

    public void generateUVs(){
        uvs = new Vector2f[vertices.length];

        for (int i = 0; i < triangles.length - 2; i += 3) {
            Vector3f norm = new Vector3f(normals[triangles[i]]); // This may also need fixing, see below

            float dotX = Math.abs(norm.dot(new Vector3f(1, 0, 0)));
            float dotY = Math.abs(norm.dot(new Vector3f(0, 1, 0)));
            float dotZ = Math.abs(norm.dot(new Vector3f(0, 0, 1)));

            for (int j = 0; j < 3; j++) {
                int triangleIndex = triangles[i + j];
                if (triangleIndex < 0 || triangleIndex >= vertices.length) continue; // Safety check

                if (dotX > dotY && dotX > dotZ) {
                    uvs[triangleIndex] = new Vector2f(vertices[triangleIndex].z, vertices[triangleIndex].y);
                } else if (dotY > dotX && dotY > dotZ) {
                    uvs[triangleIndex] = new Vector2f(vertices[triangleIndex].x, vertices[triangleIndex].z);
                } else {
                    uvs[triangleIndex] = new Vector2f(vertices[triangleIndex].x, vertices[triangleIndex].y);
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

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void cleanUp(){
        GL30.glDeleteVertexArrays(vaoID);

        vertices = null;
        uvs = null;
        normals = null;
        tangents = null;
        bitangents = null;
        triangles = null;

        for(int vbo: vbos){
            GL30.glDeleteBuffers(vbo);
        }
    }

    public boolean hasTangents(){
        return tangentsVBOID != -1 && bitangentsVBOID != -1;
    }

    public final int getInstanceVBOID(){
        return instanceVBOID;
    }

    public Mesh setInstanced(boolean instanced) {
        isInstanced = instanced;
        return this;
    }

    public void addInstanceOffset(Matrix4f offset){
        isInstanced = true;
        instanceOffsets.add(offset);
    }

    public boolean isInstanced() {
        return isInstanced;
    }

    public int getInstanceCount() {
        return instanceOffsets.size();
    }

    public void transferInstances(){
        if (instanceVBOID == -1) {

            GL30.glBindVertexArray(vaoID);
            instanceVBOID = GL15.glGenBuffers();
            vbos.add(instanceVBOID);

            FloatBuffer buffer = MemoryUtil.memAllocFloat(instanceOffsets.size() * 16);
            for (Matrix4f m : instanceOffsets) {
                buffer.put(new float[] {
                        m.m00(), m.m01(), m.m02(), m.m03(),
                        m.m10(), m.m11(), m.m12(), m.m13(),
                        m.m20(), m.m21(), m.m22(), m.m23(),
                        m.m30(), m.m31(), m.m32(), m.m33()
                });
            }
            buffer.flip();

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceVBOID);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);

            int start = 5;
            int mat4Size = 64; // bytes
            int vec4Size = 16; // bytes

            for (int i = 0; i < 4; i++) {
                GL20.glEnableVertexAttribArray(start + i);
                GL20.glVertexAttribPointer(start + i, 4, GL11.GL_FLOAT, false, mat4Size, i * vec4Size);
                GL33.glVertexAttribDivisor(start + i, 1);
            }

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL30.glBindVertexArray(0);

            unbind();
        }
    }

    public List<Matrix4f> getInstanceOffsets(){
        return instanceOffsets;
    }
}
