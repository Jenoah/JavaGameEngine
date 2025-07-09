package game.terrain;

import game.utils.ChunkCoord;
import game.utils.ChunkUtils;
import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.utils.*;
import org.joml.Math;
import org.joml.Vector3f;

import java.util.*;

public class TerrainGeneration implements Runnable{
    private final HashMap<ChunkCoord, MarchingChunk> chunks;
    private final Set<ChunkCoord> chunkGenerationQueue;
    private final Set<ChunkCoord> previousRenderQueue;
    private final Set<ChunkCoord> activeChunks;
    private final Set<MarchingChunk> marchingChunksQueue = new HashSet<>();
    private final Set<MarchingChunk> marchingChunkRequeue = new HashSet<>();

    private final int renderDistance;
    private final int verticalRenderDistance;
    private ChunkCoord previousPlayerChunkCoord;
    private boolean canUpdate = false;
    private boolean isRunning = false;
    public static long waitTime = 500;
    private Vector3f playerPosition = new Vector3f(0);

    public static float surfaceFeatureDensity = .4f;
    public static int surfaceFeatureSamples = 16;

    public TerrainGeneration(int renderDistance){
        this.renderDistance = renderDistance;
        previousPlayerChunkCoord = ChunkCoord.toChunkCoord(new Vector3f(-999));
        chunks = new HashMap<>();
        chunkGenerationQueue = new HashSet<>();
        previousRenderQueue = new HashSet<>();
        activeChunks = new HashSet<>();
        verticalRenderDistance = (int)Math.ceil(ChunkUtils.maxTerrainHeight / (float)Constants.CHUNK_SIZE);
    }

    @Override
    public void run() {
        Debug.Log("Starting terrain generation on generation thread");

        this.isRunning = true;

        synchronized (this){
            while (isRunning) {
                try {
                    if (this.canUpdate) {
                        updateChunks();
                        this.canUpdate = false;
                    }
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    Debug.Log("STOPPING THREAD: " + e);
                    isRunning = false;
                }
            }
        }
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
        previousRenderQueue.clear();
        previousRenderQueue.addAll(chunks.keySet());

        for (int x = -renderDistance; x < renderDistance; x++) {
            for (int y = 0; y < verticalRenderDistance; y++){
                for (int z = -renderDistance; z < renderDistance; z++) {
                    playerPosition.y = 0;
                    ChunkCoord chunkCoord = ChunkCoord.toChunkCoord(Calculus.addVectors(playerPosition, new Vector3f(x * Constants.CHUNK_SIZE, y * Constants.CHUNK_SIZE, z * Constants.CHUNK_SIZE)));
                    if (!chunks.containsKey(chunkCoord) && !chunkGenerationQueue.contains(chunkCoord)) {
                        chunkGenerationQueue.add(chunkCoord);
                    } else {
                        MarchingChunk chunk = chunks.get(chunkCoord);
                        if(chunk != null && chunk.isReady && !chunk.isEmpty) {
                            chunk.setActive(true);
                        }
                    }

                    activeChunks.add(chunkCoord);
                }
            }
        }


        for(ChunkCoord coord : previousRenderQueue){
            if(!activeChunks.contains(coord)){
                chunks.get(coord).setActive(false);
            }
        }


        for(ChunkCoord coord : chunkGenerationQueue){
            MarchingChunk newChunk = new MarchingChunk(coord);
            chunks.put(coord, newChunk);
            queueChunk(newChunk);
        }
    }

    public int getActiveChunkCount(){
        return activeChunks.size();
    }

    public int getTotalChunkCount(){
        return chunks.size();
    }

    public Set<MarchingChunk> getMarchingChunksQueue() {
        return marchingChunksQueue;
    }

    public Set<MarchingChunk> getMarchingChunkRequeue() {
        return marchingChunkRequeue;
    }

    public void restockQueue(){
        marchingChunksQueue.clear();
        marchingChunksQueue.addAll(marchingChunkRequeue);
        marchingChunkRequeue.clear();
    }

    public void queueChunk(MarchingChunk chunk){
        marchingChunksQueue.add(chunk);
    }

    public void requeueChunk(MarchingChunk chunk){
        marchingChunkRequeue.add(chunk);
    }

    public void setSurfaceFeatureDensity(float surfaceFeatureDensity){
        TerrainGeneration.surfaceFeatureDensity = surfaceFeatureDensity;
    }

    public void setSurfaceFeatureSamples(int surfaceFeatureSamples){
        TerrainGeneration.surfaceFeatureSamples = surfaceFeatureSamples;
    }
}
