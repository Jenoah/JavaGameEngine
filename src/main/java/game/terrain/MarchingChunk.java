package game.terrain;

import game.utils.ChunkCoord;
import game.utils.ChunkUtils;
import nl.jenoah.core.ModelManager;
import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.entity.Model;
import nl.jenoah.core.utils.Calculus;
import nl.jenoah.core.utils.Constants;
import nl.jenoah.core.utils.Conversion;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

import static org.joml.Math.lerp;

public class MarchingChunk {

    List<Vector3f> vertices = new ArrayList<>();
    List<Integer> triangles = new ArrayList<>();
    float[] terrainHeights;
    Vector3f[] normals;

    Entity chunkEntity;

    public ChunkCoord chunkPosition;
    public boolean isReady = false;


    public MarchingChunk(ChunkCoord chunkPosition){
        this.chunkPosition = chunkPosition;
        this.terrainHeights = new float[(Constants.CHUNK_SIZE + 1) * (Constants.CHUNK_SIZE + 1)];
        init();
    }

    private void init(){
        UpdateChunk();
        if(!vertices.isEmpty()) {
            normals = calculateNormals();
            isReady = true;
            //publishChunk();
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
        int[] triangleArray = triangles.stream().mapToInt(i->i).toArray();

        Model chunkModel = ModelManager.loadModel(vertices.toArray(new Vector3f[0]), null, triangleArray, normals);
        chunkModel.getMesh().generateUVs();
        chunkEntity = new Entity(chunkModel, chunkPosition.toVector3(), new Vector3f(), 1);
    }

    private void MarchCube(Vector3i voxelPosition)
    {
        //Check to see which corners of the voxel are visible
        float[] voxelCorners = new float[8];
        for (int i = 0; i < 8; i++)
        {
            Vector3i cornerPosition = Calculus.addVectors(voxelPosition, Constants.cornerTable[i]);
            //voxelCorners[i] = voxels[cornerPosition.x][cornerPosition.y][cornerPosition.z];

            // Sample height at this corner position (use chunk position to offset properly)
            float heightSample = ChunkUtils.SampleHeight(chunkPosition.x + cornerPosition.x, chunkPosition.z + cornerPosition.z) - chunkPosition.y;

            // Store the voxel value (this is the y-coordinate minus the sampled height)
            voxelCorners[i] = cornerPosition.y - heightSample;
        }

        //See which shape of Marching cube the current voxel should get
        int configIndex = ChunkUtils.GetVoxelConfiguration(voxelCorners);

        //Return if the voxel will not have any vertices
        if (configIndex == 0 || configIndex == 255) return;

        //Calculate vertices that correspond to the index of the voxel
        int edgeIndex = 0;
        for (int t = 0; t < 5; t++)
        {
            for (int p = 0; p < 3; p++)
            {
                int indice = Constants.triangleTable[configIndex][edgeIndex];

                if (indice == -1) return;

                //Get start and end of the edge between the vertices
                Vector3f vertex1 = new Vector3f(Calculus.addVectors(voxelPosition, Constants.cornerTable[Constants.edgeIndexes[indice][0]]));
                Vector3f vertex2 = new Vector3f(Calculus.addVectors(voxelPosition, Constants.cornerTable[Constants.edgeIndexes[indice][1]]));

                Vector3f vertexPosition;
                //Get the center of the edge

                if (ChunkUtils.smoothTerrain)
                {
                    float difference = getVertexDifference(voxelCorners, indice);

                    vertexPosition = Calculus.addVectors(vertex1, Calculus.multiplyVector(Calculus.subtractVectors(vertex2, vertex1), difference));
                    triangles.add(VertForIndex(vertexPosition));
                }
                else
                {
                    vertexPosition = Calculus.multiplyVector(Calculus.addVectors(vertex1,vertex2), 0.5f);
                    vertices.add(vertexPosition);
                    triangles.add(vertices.size() - 1);
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
        if (difference == 0f)
        {
            difference = ChunkUtils.terrainSurfaceHeight;
        }
        else
        {
            difference = (ChunkUtils.terrainSurfaceHeight - vertex1Sample) / difference;
        }
        return difference;
    }

    private Vector3f[] calculateNormals() {
        Vector3f[] normals = new Vector3f[vertices.size()];

        for(int i = 0; i < vertices.size(); i++) {
            Vector3f vertex = vertices.get(i);
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

    private int VertForIndex(Vector3f vert)
    {
        for (int i = 0; i < vertices.size(); i++)
        {
            if (vertices.get(i) == vert) return i;
        }
        vertices.add(vert);
        return vertices.size() - 1;
    }

    public Entity getChunkEntity(){
        return chunkEntity;
    }

    private float getScalar(int x, int y, int z) {

        float heightSample = ChunkUtils.SampleHeight(chunkPosition.x + x, chunkPosition.z + z) - chunkPosition.y;
        return y - heightSample;
    }
}
