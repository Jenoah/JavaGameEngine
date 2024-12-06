package nl.jenoah.core;

import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.utils.Constants;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;

public class WindowManager {
    private final String title;

    private int width, height;
    private long window;

    private boolean resize, vSync;

    private final Matrix4f projectionMatrix;

    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        projectionMatrix = new Matrix4f();
    }

    public void init(){
        Debug.Log("Initializing window");
        GLFWErrorCallback.createPrint(System.err).set();

        if(!GLFW.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        boolean maximized = false;
        if(width == 0 || height == 0){
            width = 100;
            height = 100;
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            maximized = true;
        }

        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if(window == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create GLFW window");

        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.setResize(true);
        });

        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
                GLFW.glfwSetWindowShouldClose(window, true);
        });

        if(maximized){
            GLFW.glfwMaximizeWindow(window);
        }else{
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
        }

        GLFW.glfwMakeContextCurrent(window);

        if(isvSync())
            GLFW.glfwSwapInterval(1);

        GLFW.glfwShowWindow(window);

        GL.createCapabilities();

        glClearColor(0,0,0, 0);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public void update(){
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    // INPUT

    public boolean isKeyPressed(int keyCode){
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
    }

    // WINDOW
    public boolean isResize(){
        return resize;
    }

    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public boolean isvSync(){
        return vSync;
    }

    public void setResize(boolean resize){
        this.resize = resize;
    }

    public void cleanUp(){
        GLFW.glfwDestroyWindow(window);
    }

    public String getTitle() {
        return title;
    }

    public void setWindowTitle(String title){
        GLFW.glfwSetWindowTitle(window, title);
    }

    public long getWindow() {
        return window;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    //RENDERING
    public void setClearColor(float r, float g, float b, float a){
        GL11.glClearColor(r,g,b,a);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix(){
        float aspectRatio = (float) width / height;

        return projectionMatrix.setPerspective(Constants.FOV, aspectRatio, Constants.Z_NEAR, Constants.Z_FAR);
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height){
        float aspectRatio = (float) width / height;
        return matrix.setPerspective(Constants.FOV, aspectRatio, width, height);
    }
}
