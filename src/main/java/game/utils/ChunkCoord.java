package game.utils;

import nl.jenoah.core.utils.Constants;
import org.joml.Vector3f;

public class ChunkCoord{
    public int x, y, z;
    
    public ChunkCoord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static ChunkCoord toChunkCoord(Vector3f vector){
        int x, y, z;
        x = fastfloor((int)vector.x / (float) Constants.CHUNK_SIZE) * Constants.CHUNK_SIZE;
        y = fastfloor((int)vector.y / (float)Constants.CHUNK_SIZE) * Constants.CHUNK_SIZE;
        z = fastfloor((int)vector.z / (float)Constants.CHUNK_SIZE) * Constants.CHUNK_SIZE;

        return new ChunkCoord(x, y, z);
    }
    
    public Vector3f toVector3(){
        return new Vector3f(x, y, z);
    }

    private static int fastfloor(double x) {
        int xi = (int)x;
        return x<xi ? xi-1 : xi;
    }

    public static boolean isEqual(ChunkCoord a, ChunkCoord b){
        return a.x == b.x && a.y == b.y && a.z == b.z;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof ChunkCoord b)) return false;
        return this.x == b.x && this.y == b.y && this.z == b.z;
    }

    @Override
    public final int hashCode() {
        return this.x ^ this.y << 2 ^ this.z >> 2;
    }

    @Override
    public String toString() {
        return "X " + x + ", Y " + y + ", Z" + z;
    }
}
