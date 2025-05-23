package nl.jenoah.core;

import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.Texture;
import nl.jenoah.core.loaders.TextureLoader;
import nl.jenoah.core.utils.Constants;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;
import static org.lwjgl.system.MemoryUtil.memAllocInt;

public class WindowManager {
    private final String title;

    private int width, height;
    private long window;

    private boolean resize;
    private final boolean vSync;

    private final Matrix4f projectionMatrix;

    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        projectionMatrix = new Matrix4f();
        instance = this;
    }

    private static WindowManager instance = null;

    public static synchronized WindowManager getInstance()
    {
        if (instance == null) {
            Debug.Log("Window manager not set");
        }

        return instance;
    }

    public void init(){
        Debug.Log("Initializing window");
        GLFWErrorCallback.createPrint(System.err).set();

        if(!GLFW.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

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

        GLFW.glfwSwapInterval(isvSync() ? 1 : 0);

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

    public void setWindowIcon(String path){
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("mac")) return;

        IntBuffer w = memAllocInt(1);
        IntBuffer h = memAllocInt(1);
        IntBuffer comp = memAllocInt(1);

        try (GLFWImage.Buffer icons = GLFWImage.malloc(1)) {

            ByteBuffer pixels16 = STBImage.stbi_load(path, w, h, comp, 4);
            icons
                    .position(0)
                    .width(w.get(0))
                    .height(h.get(0))
                    .pixels(pixels16);

            icons.position(0);
            glfwSetWindowIcon(window, icons);
        }
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
        glClearColor(r,g,b,a);
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
