package game.terrain;

import game.utils.ChunkCoord;
import game.utils.ChunkUtils;
import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.utils.Calculus;
import nl.jenoah.core.utils.Constants;
import org.joml.Math;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

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
                        Entity chunkEntity = chunks.get(chunkCoord).getChunkEntity();
                        if(chunkEntity != null) chunkEntity.setEnabled(true);
                    }

                    activeChunks.add(chunkCoord);
                }
            }
        }

        while(!previousRenderQueue.isEmpty()){
            ChunkCoord chunkCoord = previousRenderQueue.poll();
            if(!activeChunks.contains(chunkCoord)){
                Entity chunkEntity = chunks.get(chunkCoord).getChunkEntity();
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
}
