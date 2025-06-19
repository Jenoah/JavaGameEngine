package game.terrain;

import game.utils.ChunkCoord;
import game.utils.ChunkUtils;
import nl.jenoah.core.ModelManager;
import nl.jenoah.core.components.RenderComponent;
import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.GameObject;
import nl.jenoah.core.entity.Model;
import nl.jenoah.core.utils.Calculus;
import nl.jenoah.core.utils.Constants;
import nl.jenoah.core.utils.Transformation;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.*;

import static org.joml.Math.lerp;

public class MarchingChunk {

    private static final int MAX_VERTICES = 4096; // Estimate or calculate max needed
    private static final int MAX_TRIANGLES = 8192;

    Vector3f[] vertices = new Vector3f[MAX_VERTICES];
    int[] triangles = new int[MAX_TRIANGLES];

    int vertexCount = 0;
    int triangleCount = 0;

    float[] terrainHeights;
    Vector3f[] normals;

    GameObject chunkEntity;

    public ChunkCoord chunkPosition;
    public boolean isReady = false;
    public boolean isEmpty = false;

    Set<Matrix4f> surfaceFeatureModelMatrices = new HashSet<>();

    public MarchingChunk(ChunkCoord chunkPosition){
        this.chunkPosition = chunkPosition;
        this.terrainHeights = new float[(Constants.CHUNK_SIZE + 1) * (Constants.CHUNK_SIZE + 1)];
        init();
    }

    private void init(){
        UpdateChunk();
        if(vertexCount > 0) {
            normals = calculateNormals();
            isReady = true;
        }else{
            isEmpty = true;
            isReady = true;
        }
    }

    private void UpdateChunk(){
        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                for (int z = 0; z < Constants.CHUNK_SIZE; z++) {
                    terrainHeights[x * (Constants.CHUNK_SIZE + 1) + z] = ChunkUtils.SampleHeight(chunkPosition.x + x, chunkPosition.z + z) + ChunkUtils.terrainSurfaceHeight;
                    MarchCube(new Vector3i(x, y, z));
                }
            }
        }
    }

    public void publishChunk(){
        if(vertexCount == 0) return;

        Model chunkModel = ModelManager.loadModel(Arrays.copyOf(vertices, vertexCount), null, Arrays.copyOf(triangles, triangleCount), normals);
        chunkModel.getMesh().generateUVs();
        chunkEntity = new GameObject().setPosition(chunkPosition.toVector3());
        chunkEntity.addComponent(new RenderComponent(chunkModel.getMesh(), chunkModel.getMaterial()));
    }

    private void MarchCube(Vector3i voxelPosition) {
        float[] voxelCorners = new float[8];
        for (int i = 0; i < 8; i++) {
            Vector3i cornerPosition = Calculus.addVectors(voxelPosition, Constants.cornerTable[i]);
            float heightSample = ChunkUtils.SampleHeight(chunkPosition.x + cornerPosition.x, chunkPosition.z + cornerPosition.z) - chunkPosition.y;
            voxelCorners[i] = cornerPosition.y - heightSample;
        }

        int configIndex = ChunkUtils.GetVoxelConfiguration(voxelCorners);
        if (configIndex == 0 || configIndex == 255) return;

        Vector3f vertex1;
        Vector3f vertex2;
        Vector3f vertexPosition;

        int edgeIndex = 0;
        for (int t = 0; t < 5; t++) {
            for (int p = 0; p < 3; p++) {
                int indice = Constants.triangleTable[configIndex][edgeIndex];
                if (indice == -1) return;

                vertex1 = Calculus.addVectorsF(voxelPosition, Constants.cornerTable[Constants.edgeIndexes[indice][0]]);
                vertex2 = Calculus.addVectorsF(voxelPosition, Constants.cornerTable[Constants.edgeIndexes[indice][1]]);

                if (ChunkUtils.smoothTerrain) {
                    float difference = getVertexDifference(voxelCorners, indice);
                    vertexPosition = new Vector3f(vertex2).sub(vertex1).mul(difference).add(vertex1);
                    addTriangle(VertForIndex(vertexPosition));
                } else {
                    vertexPosition = new Vector3f(vertex1).add(vertex2).mul(0.5f);
                    addVertex(new Vector3f(vertexPosition));
                    addTriangle(vertexCount - 1);
                }
                edgeIndex++;
            }
        }
    }

    public float getHeightAt(float x, float z) {
        // Use floor() and ceil() with grid alignment
        int x0 = (int)x;
        int z0 = (int)z;
        int x1 = x0 + 1;
        int z1 = z0 + 1;

        // Clamp using (CHUNK_SIZE + 1) for grid points
        x0 = Math.max(0, Math.min(x0, Constants.CHUNK_SIZE));
        x1 = Math.max(0, Math.min(x1, Constants.CHUNK_SIZE));
        z0 = Math.max(0, Math.min(z0, Constants.CHUNK_SIZE));
        z1 = Math.max(0, Math.min(z1, Constants.CHUNK_SIZE));

        float sx = x - x0;
        float sz = z - z0;

        // Use (CHUNK_SIZE + 1) for grid point alignment
        float h00 = terrainHeights[x0 * (Constants.CHUNK_SIZE + 1) + z0];
        float h10 = terrainHeights[x1 * (Constants.CHUNK_SIZE + 1) + z0];
        float h01 = terrainHeights[x0 * (Constants.CHUNK_SIZE + 1) + z1];
        float h11 = terrainHeights[x1 * (Constants.CHUNK_SIZE + 1) + z1];

        return lerp(lerp(h00, h10, sx), lerp(h01, h11, sx), sz);
    }

    private static float getVertexDifference(float[] voxelCorners, int index) {
        float vertex1Sample = voxelCorners[Constants.edgeIndexes[index][0]];
        float vertex2Sample = voxelCorners[Constants.edgeIndexes[index][1]];

        float difference = vertex2Sample - vertex1Sample;
        return difference == 0f ? ChunkUtils.terrainSurfaceHeight : (ChunkUtils.terrainSurfaceHeight - vertex1Sample) / difference;
    }

    private Vector3f[] calculateNormals() {
        Vector3f[] normals = new Vector3f[vertexCount];

        for(int i = 0; i < vertexCount; i++) {
            Vector3f vertex = vertices[i];
            int x = (int) vertex.x;
            int y = (int) vertex.y;
            int z = (int) vertex.z;

            // Calculate the gradient using central differences
            float dx = (getScalar(x + 1, y, z) - getScalar(x - 1, y, z)) * 0.5f;
            float dy = (getScalar(x, y + 1, z) - getScalar(x, y - 1, z)) * 0.5f;
            float dz = (getScalar(x, y, z + 1) - getScalar(x, y, z - 1)) * 0.5f;

            // Normalize the gradient vector to get the normal
            float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (length > 0.0f) {
                dx /= length;
                dy /= length;
                dz /= length;
            }

            normals[i] = new Vector3f(dx, dy, dz);
        }

        return normals;
    }

    private int VertForIndex(Vector3f vert) {
        for (int i = 0; i < vertexCount; i++)
        {
            if (vertices[i] == vert) return i;
        }
        addVertex(vert);
        return vertexCount - 1;
    }

    public GameObject getChunkEntity(){ return chunkEntity; }

    private float getScalar(int x, int y, int z) {
        float heightSample = ChunkUtils.SampleHeight(chunkPosition.x + x, chunkPosition.z + z) - chunkPosition.y;
        return y - heightSample;
    }

    public Vector3f getNormalAt(float x, float z) {
        x = Math.max(1, Math.min(x, Constants.CHUNK_SIZE - 1));
        z = Math.max(1, Math.min(z, Constants.CHUNK_SIZE - 1));

        // Sample 4-connected neighbors
        float right = getHeightAt(x + 1, z);
        float left = getHeightAt(x - 1, z);
        float up = getHeightAt(x, z + 1);
        float down = getHeightAt(x, z - 1);

        // Sobel operator for better quality
        Vector3f tangentX = new Vector3f(
                2.0f,
                (right - left),
                0.0f
        );

        Vector3f tangentZ = new Vector3f(
                0.0f,
                (up - down),
                2.0f
        );

        return tangentZ.cross(tangentX).normalize();
    }

    public void setActive(boolean active){
        if(chunkEntity == null) return;
        chunkEntity.setEnabled(active);
    }

    public void addSurfaceFeature(Matrix4f modelMatrix){
        surfaceFeatureModelMatrices.add(modelMatrix);
    }

    public void addSurfaceFeature(Vector3f position, Quaternionf rotation, Vector3f scale){
        surfaceFeatureModelMatrices.add(Transformation.toModelMatrix(position, rotation, scale));
    }

    public final Set<Matrix4f> getSurfaceFeatureMatrices(){
        return surfaceFeatureModelMatrices;
    }

    private void addVertex(Vector3f v) {
        if (vertexCount >= vertices.length) {
            Debug.LogError("Marching chunk vertexCount exceeds vertex array length");
        }
        vertices[vertexCount++] = v;
    }

    private void addTriangle(int idx) {
        if (triangleCount >= triangles.length) {
            Debug.LogError("Marching chunk triangleCount exceeds triangles array length");
        }
        triangles[triangleCount++] = idx;
    }
}
