package nl.jenoah.core;

import nl.jenoah.core.entity.SceneManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {

    public static final long NANOSECOND = 1000000000L;
    public static final float FRAMERATE = 1000;

    private static int fps;
    private static int currentFrameCount = 0;

    private boolean isRunning;

    private WindowManager window;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;
    private MouseInput mouseInput;

    private void init(ILogic gameLogic) throws Exception{
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window =  WindowManager.getInstance();
        this.gameLogic = gameLogic;
        mouseInput = new MouseInput();
        window.init();
        gameLogic.init();
        mouseInput.init();
        SceneManager.getInstance().getCurrentScene().postStart();
    }

    public void start(ILogic gameLogic) throws Exception{
        init(gameLogic);
        if(isRunning)
            return;
        System.out.println("Starting Engine...");
        run();
    }

    public void run(){
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
    }

    public void stop(){
        if(!isRunning)
            return;
        isRunning = false;
    }

    public void input(){
        mouseInput.input();
        gameLogic.input();
    }

    public void render(){
        gameLogic.render();
        window.update();
    }

    public void update(float interval){
        gameLogic.update(interval, mouseInput);
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

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }

    public static int getFrameCount(){
        return currentFrameCount;
    }
}
