package nl.jenoah.core;

import nl.jenoah.core.debugging.Debug;

public class Settings {
    private static boolean useAnisotropic = true;
    private static boolean useVSync = true;
    private static int MSAASamples = 8;
    private static float gamma = 2.2f;


    public static boolean isUseAnisotropic() {
        return useAnisotropic;
    }

    public static boolean isUseVSync() {
        return useVSync;
    }

    public static float getGamma() {
        return gamma;
    }

    public static int getMSAASamples() {
        Debug.Log("To implement MSAA with FBOs, follow https://www.youtube.com/watch?v=HCBoIvVmYgk");
        return MSAASamples;
    }
}
