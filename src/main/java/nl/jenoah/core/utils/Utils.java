package nl.jenoah.core.utils;

import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Utils {
    public static SimplexNoise simplexNoise = new SimplexNoise();
    public static FastNoise fastNoise = new FastNoise();

    public static FloatBuffer storeDataInFloatBuffer(float[] data){
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static void setNoiseSeed(int seed){
        SimplexNoise.RANDOMSEED = seed;
        simplexNoise = new SimplexNoise();
        fastNoise.SetSeed(seed);
    }

    public static IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }
}
