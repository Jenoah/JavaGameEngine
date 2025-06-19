package nl.jenoah.core;

import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.SceneManager;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class EngineManager {
    private boolean running = false;

    private WindowManager window;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;
    private MouseInput mouseInput;

    // Consider making these instance fields if multiple EngineManagers are possible
    private double lastLoopTime = 0.0;
    private float timeAccumulator = 0.0f;
    private static int fps = 0;
    public static int frameCount = 0;
    private static float deltaTime = 0.0f;
    private static float frameTime = 0.0f;
    private static float spareTime = 0.0f;

    private static final float MIN_DELTA_TIME = 1.0f / 1000f; // 1ms minimum, adjust as needed

    private void init(final ILogic gameLogic) throws Exception {
        errorCallback = GLFWErrorCallback.createPrint(System.err);
        GLFW.glfwSetErrorCallback(errorCallback);

        window = WindowManager.getInstance();
        this.gameLogic = gameLogic;
        mouseInput = new MouseInput();

        window.init();
        gameLogic.init();
        mouseInput.init();
        SceneManager.getInstance().getCurrentScene().postStart();

        lastLoopTime = getCurrentTime();
    }

    public void start(final ILogic gameLogic) throws Exception {
        if (running) return;
        init(gameLogic);
        Debug.Log("Starting Engine...");
        run();
    }

    public void run() {
        running = true;
        try {
            while (running) {
                if (window.windowShouldClose()) stop();

                double frameStart = getCurrentTime();

                updateDeltaTime();
                handleInput();
                updateGame();
                renderFrame();

                double frameEnd = getCurrentTime();
                frameTime = (float)(frameEnd - frameStart);
                spareTime = Math.max(0f, getDeltaTime() - frameTime); // Ensure spare time is non-negative
            }
        } finally {
            cleanup();
        }
    }

    public void stop() {
        running = false;
    }

    private void handleInput() {
        mouseInput.input();
        gameLogic.input();
    }

    private void updateGame() {
        gameLogic.update(deltaTime, mouseInput);

        frameCount++;
        timeAccumulator += deltaTime;

        if (timeAccumulator >= 1.0f) {
            fps = frameCount;
            frameCount = 0;
            timeAccumulator -= 1.0f;
        }
    }

    private void renderFrame() {
        gameLogic.render();
        window.update();
    }

    private void cleanup() {
        window.cleanUp();
        gameLogic.cleanUp();
        if (errorCallback != null) errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static double getCurrentTime() {
        return GLFW.glfwGetTime();
    }

    private void updateDeltaTime() {
        final double currentTime = getCurrentTime();
        deltaTime = (float) (currentTime - lastLoopTime);
        deltaTime = Math.max(deltaTime, MIN_DELTA_TIME); // Clamp to avoid extremely small values
        lastLoopTime = currentTime;
    }

    public static float getDeltaTime() {
        return deltaTime;
    }

    public static float getDeltaTimeMS() {
        return Math.round(deltaTime * 100000f) / 100f;
    }

    public static float getFrameTime() {
        return frameTime;
    }

    public static float getFrameTimeMS() {
        return Math.round(frameTime * 100000f) / 100f;
    }

    public static float getSpareTime() {
        return spareTime;
    }
    public static float getSpareTimeMS() {
        return Math.round(spareTime * 100000f) / 100f;
    }
}