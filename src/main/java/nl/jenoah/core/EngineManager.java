package nl.jenoah.core;

import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.SceneManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class EngineManager {
    private boolean isRunning;

    private WindowManager window;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;
    private MouseInput mouseInput;

    private static double lastLoopTime = 0;
    private static float timeCount = 0;
    private static int fps;
    public static int fpsCount;
    private int ups;
    private int upsCount;
    private static float deltaTime;

    private void init(ILogic gameLogic) throws Exception{
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window =  WindowManager.getInstance();
        this.gameLogic = gameLogic;
        mouseInput = new MouseInput();
        window.init();
        gameLogic.init();
        mouseInput.init();
        SceneManager.getInstance().getCurrentScene().postStart();
        lastLoopTime = getTime();
    }

    public void start(ILogic gameLogic) throws Exception{
        init(gameLogic);
        if(isRunning)
            return;
        Debug.Log("Starting Engine...");
        run();
    }

    public void run(){
        isRunning = true;
        while(isRunning){
            if(window.windowShouldClose()) stop();

            setDeltaTime();
            deltaTime = getDeltaTime();
            input();
            update(deltaTime);
            render();
        }
        cleanUp();
    }

    /*
    public void runOLD(){
        this.isRunning = true;

        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while(isRunning){
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double)NANOSECOND;
            frameCounter += passedTime;

            float frameTime = 1.0f / FRAMERATE;
            while(unprocessedTime > frameTime){
                render = true;
                unprocessedTime -= frameTime;

                if(window.windowShouldClose()){
                    stop();
                }

                if(frameCounter >= NANOSECOND) {
                    setFps(frames);
                    //window.setWindowTitle(Constants.TITLE + " - FPS: " + getFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if(render){
                input();
                update(frameTime);
                render();
                frames++;
                currentFrameCount++;
            }
        }

        cleanUp();
    }*/

    private void stop(){
        if(!isRunning)
            return;
        isRunning = false;
    }

    private void input(){
        mouseInput.input();
        gameLogic.input();
    }

    private void render(){
        gameLogic.render();
        window.update();
    }

    private void update(float interval){
        gameLogic.update(interval, mouseInput);
        if (timeCount > 1f) {
            fps = fpsCount;
            fpsCount = 0;

            ups = upsCount;
            upsCount = 0;

            timeCount -= 1f;
        }
        updateFPS();
        updateUPS();
    }

    public void cleanUp(){
        window.cleanUp();
        gameLogic.cleanUp();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static double getTime() {
        return glfwGetTime();
    }

    private void setDeltaTime() {
        double time = getTime();
        float delta = (float) (time - lastLoopTime);
        lastLoopTime = time;
        timeCount += delta;
        deltaTime = delta;
    }

    public static float getDeltaTime(){
        return deltaTime;
    }

    private void updateFPS() {
        fpsCount++;
    }

    private void updateUPS() {
        upsCount++;
    }
}
