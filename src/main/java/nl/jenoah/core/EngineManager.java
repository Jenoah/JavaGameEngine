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

    private static double lastLoopTime = 0.0;
    private static float timeAccumulator = 0.0f;
    private static int fps = 0;
    public static int frameCount = 0;
    private static float deltaTime = 0.0f;
    private static float frameTime = 0.0f;
    private static float spareTime = 0.0f;

    /**
     * Initializes the engine and all subsystems.
     */
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

    /**
     * Starts the engine loop.
     */
    public void start(final ILogic gameLogic) throws Exception {
        if (running) return;
        init(gameLogic);
        Debug.Log("Starting Engine...");
        run();
    }

    /**
     * The main engine loop.
     */
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
                spareTime = getDeltaTime() - frameTime;
            }
        } finally {
            cleanup();
        }
    }

    /**
     * Stops the engine loop.
     */
    public void stop() {
        running = false;
    }

    /**
     * Handles user input.
     */
    private void handleInput() {
        mouseInput.input();
        gameLogic.input();
    }

    /**
     * Updates game logic and FPS counter.
     */
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

    /**
     * Renders a frame.
     */
    private void renderFrame() {
        gameLogic.render();
        window.update();
    }

    /**
     * Cleans up resources.
     */
    private void cleanup() {
        window.cleanUp();
        gameLogic.cleanUp();
        if (errorCallback != null) errorCallback.free();
        GLFW.glfwTerminate();
    }

    /**
     * @return the most recent FPS value.
     */
    public static int getFps() {
        return fps;
    }

    /**
     * @return the current time in seconds.
     */
    public static double getCurrentTime() {
        return GLFW.glfwGetTime();
    }

    /**
     * Updates deltaTime for the current frame.
     */
    private void updateDeltaTime() {
        final double currentTime = getCurrentTime();
        deltaTime = (float) (currentTime - lastLoopTime);
        lastLoopTime = currentTime;
    }

    /**
     * @return deltaTime in seconds.
     */
    public static float getDeltaTime() {
        return deltaTime;
    }

    /**
     * @return deltaTime in milliseconds, rounded to two decimals.
     */
    public static float getDeltaTimeMS() {
        return Math.round(deltaTime * 100000f) / 100f;
    }

    /**
     * @return The time in seconds it took to process the last frame (excluding waiting).
     */
    public static float getFrameTime() {
        return frameTime;
    }

    /**
     * @return The time in milliseconds it took to process the last frame (rounded to 2 decimals).
     */
    public static float getFrameTimeMS() {
        return Math.round(frameTime * 100000f) / 100f;
    }

    public static float getSpareTime() { return spareTime; }
    public static float getSpareTimeMS() { return Math.round(spareTime * 100000f) / 100f; }
}