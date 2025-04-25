package game.terrain;

import game.utils.ChunkCoord;
import game.utils.ChunkUtils;
import nl.jenoah.core.components.RenderComponent;
import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.GameObject;
import nl.jenoah.core.rendering.MeshMaterialSet;
import nl.jenoah.core.utils.*;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class TerrainGeneration extends Thread{
    private HashMap<ChunkCoord, MarchingChunk> chunks = new HashMap<ChunkCoord, MarchingChunk>();
    private final Queue<ChunkCoord> chunkGenerationQueue;
    private Queue<ChunkCoord> previousRenderQueue;
    private final Queue<ChunkCoord> activeChunks;

    private Queue<MarchingChunk> marchingChunksQueue = new ArrayDeque<>();
    private final Queue<MarchingChunk> marchingChunkRequeue = new ArrayDeque<>();


    private final int renderDistance;
    private final int verticalRenderDistance;
    private ChunkCoord previousPlayerChunkCoord;
    private boolean canUpdate = false;
    private boolean isRunning = false;
    public static long waitTime = 300;
    private Vector3f playerPosition = new Vector3f(0);

    private float surfaceFeatureDensity = .4f;
    private int surfaceFeatureSamples = 16;
    private GameObject surfaceFeatureEntity = null;

    public TerrainGeneration(int renderDistance){
        this.renderDistance = renderDistance;
        previousPlayerChunkCoord = ChunkCoord.toChunkCoord(new Vector3f(-999));
        chunks = new HashMap<ChunkCoord, MarchingChunk>();
        chunkGenerationQueue = new ArrayDeque<>();
        previousRenderQueue = new ArrayDeque<>();
        activeChunks = new ArrayDeque<>();
        verticalRenderDistance = (int)Math.ceil(ChunkUtils.maxTerrainHeight / (float)Constants.CHUNK_SIZE);
    }

    @Override
    public void run() {
        Debug.Log("Starting terrain generation on generation thread");

        this.isRunning = true;

        while(isRunning){
            try{
                if(this.canUpdate){
                    updateChunks();
                    canUpdate = false;
                }
                Thread.sleep(waitTime);
            }catch (InterruptedException e){
                Debug.Log("STOPPING THREAD: " + e);
                isRunning = false;
            }
        }
    }

    public void setSurfaceFeature(GameObject surfaceFeatureEntity){
        this.surfaceFeatureEntity = surfaceFeatureEntity;
    }

    public void end(){
        this.isRunning = false;
    }

    public void setUpdatePosition(Vector3f updatePosition){
        playerPosition = updatePosition;
        if(!ChunkCoord.compareToVector(previousPlayerChunkCoord, playerPosition)){
            previousPlayerChunkCoord = ChunkCoord.toChunkCoord(updatePosition);
            this.canUpdate = true;
        }
    }

    private void updateChunks(){
        chunkGenerationQueue.clear();
        activeChunks.clear();
        previousRenderQueue = new ArrayDeque<>(chunks.keySet());

        for (int x = -renderDistance; x < renderDistance; x++) {
            for (int y = 0; y < verticalRenderDistance; y++){
                for (int z = -renderDistance; z < renderDistance; z++) {
                    Vector3f playerPos = new Vector3f(playerPosition);
                    playerPos.y = 0;
                    ChunkCoord chunkCoord = ChunkCoord.toChunkCoord(Calculus.addVectors(playerPos, new Vector3f(x * Constants.CHUNK_SIZE, y * Constants.CHUNK_SIZE, z * Constants.CHUNK_SIZE)));
                    if (!chunks.containsKey(chunkCoord) && !chunkGenerationQueue.contains(chunkCoord)) {
                        chunkGenerationQueue.add(chunkCoord);
                    } else {
                        MarchingChunk chunk = chunks.get(chunkCoord);
                        if(chunk == null || !chunk.isReady) continue;
                        GameObject chunkEntity = chunks.get(chunkCoord).getChunkEntity();
                        if(chunkEntity != null) chunkEntity.setEnabled(true);
                    }

                    activeChunks.add(chunkCoord);
                }
            }
        }

        while(!previousRenderQueue.isEmpty()){
            ChunkCoord chunkCoord = previousRenderQueue.poll();
            if(!activeChunks.contains(chunkCoord)){
                GameObject chunkEntity = chunks.get(chunkCoord).getChunkEntity();
                if(chunkEntity != null) chunkEntity.setEnabled(false);
            }
        }

        while(!chunkGenerationQueue.isEmpty()){
            ChunkCoord chunkCoord = chunkGenerationQueue.poll();
            MarchingChunk newChunk = new MarchingChunk(chunkCoord);
            chunks.put(chunkCoord, newChunk);
            queueChunk(newChunk);
        }
    }

    public int getActiveChunkCount(){
        return activeChunks.size();
    }

    public int getTotalChunkCount(){
        return chunks.size();
    }

    public Queue<MarchingChunk> getMarchingChunksQueue() {
        return marchingChunksQueue;
    }

    public Queue<MarchingChunk> getMarchingChunkRequeue() {
        return marchingChunkRequeue;
    }

    public void restockQueue(){
        marchingChunksQueue = new ArrayDeque<>(marchingChunkRequeue);
        marchingChunkRequeue.clear();
    }

    public void queueChunk(MarchingChunk chunk){
        marchingChunksQueue.add(chunk);
    }

    public void requeueChunk(MarchingChunk chunk){
        marchingChunkRequeue.add(chunk);
    }

    public void setSurfaceFeatureDensity(float surfaceFeatureDensity){
        this.surfaceFeatureDensity = surfaceFeatureDensity;
    }

    public void setSurfaceFeatureSamples(int surfaceFeatureSamples){
        this.surfaceFeatureSamples = surfaceFeatureSamples;
    }

    public void addSurfaceFeatures(MarchingChunk chunk){
        if(surfaceFeatureEntity == null) return;

        // Get the original mesh/material sets to copy from
        List<MeshMaterialSet> originalMeshMaterialSets = surfaceFeatureEntity
                .getComponent(RenderComponent.class)
                .getMeshMaterialSets();

        float stepSize = (float) Constants.CHUNK_SIZE / surfaceFeatureSamples;

        List<MeshMaterialSet> tempMeshMaterialSets = new ArrayList<>(originalMeshMaterialSets.size());

        for (int x = 0; x < surfaceFeatureSamples; x++) {
            for (int z = 0; z < surfaceFeatureSamples; z++) {

                float localPositionX = (float) x * stepSize;
                float localPositionZ = (float) z * stepSize;

                float noiseLocationX = chunk.chunkPosition.x + localPositionX;
                float noiseLocationZ = chunk.chunkPosition.z + localPositionZ;
                float spawnChance = Utils.fastNoise.GetNoise(noiseLocationX, noiseLocationZ) + 1f / 2f; //TODO: ADD BRACKETS TO FIX THIS FORMULA ;)

                if(spawnChance > surfaceFeatureDensity){
                    float sampleLocationX = noiseLocationX - chunk.chunkPosition.x;
                    float sampleLocationZ = noiseLocationZ - chunk.chunkPosition.z;
                    float spawnHeight = chunk.getHeightAt(sampleLocationX, sampleLocationZ);
                    float rotationY = (spawnChance - surfaceFeatureDensity) * 360f / (1f - surfaceFeatureDensity);

                    Quaternionf normalUp = new Quaternionf().rotationTo(Constants.VECTOR3_UP, chunk.getNormalAt(sampleLocationX, sampleLocationZ));
                    normalUp.rotateY(rotationY);

                    GameObject surfaceFeatureInstance = new GameObject().setPosition(new Vector3f(localPositionX, spawnHeight, localPositionZ)).setRotation(normalUp);
                    tempMeshMaterialSets.clear();
                    for (MeshMaterialSet meshMaterialSet : originalMeshMaterialSets) {
                        tempMeshMaterialSets.add(new MeshMaterialSet(meshMaterialSet.mesh, meshMaterialSet.material));
                    }
                    RenderComponent renderComponent = new RenderComponent(new ArrayList<>(tempMeshMaterialSets));

                    surfaceFeatureInstance.addComponent(renderComponent);
                    renderComponent.initiate();

                    surfaceFeatureInstance.setParent(chunk.chunkEntity);
                }
            }
        }
    }
}
