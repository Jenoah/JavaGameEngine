package nl.jenoah.core;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class MouseInput {

    private static final Vector2d previousPosition = new Vector2d(-1);
    private static final Vector2d currentPosition = new Vector2d(-1);
    private static final Vector2f mouseDelta = new Vector2f(0);

    private static boolean lbDown = false;
    private static boolean rbDown = false;
    private static long windowLong;
    private static WindowManager window;

    public MouseInput(){
        window = WindowManager.getInstance();
        windowLong = GLFW.glfwGetCurrentContext();
    }

    public void init(){
        GLFWCursorPosCallback imguiCursorPosCallback = GLFW.glfwSetCursorPosCallback(this.window.getWindow(), null);
        GLFWMouseButtonCallback imguiMouseButtonCallback = GLFW.glfwSetMouseButtonCallback(this.window.getWindow(), null);

        GLFW.glfwSetCursorPosCallback(this.window.getWindow(), (window, xPos, yPos) -> {
            if(this.window.getFocus()) {
                currentPosition.x = xPos;
                currentPosition.y = yPos;
            }
            if (imguiCursorPosCallback != null) imguiCursorPosCallback.invoke(window, xPos, yPos);
        });

        GLFW.glfwSetMouseButtonCallback(this.window.getWindow(), (window, button, action, mods) -> {
            if(this.window.getFocus()) {
                lbDown = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
                rbDown = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
            }
            if (imguiMouseButtonCallback != null) imguiMouseButtonCallback.invoke(window, button, action, mods);
        });
    }

    public void input(){
        mouseDelta.x = 0;
        mouseDelta.y = 0;

        if(previousPosition.x > 0 && previousPosition.y > 0){
            double xDelta = currentPosition.x - previousPosition.x;
            double yDelta = currentPosition.y - previousPosition.y;
            boolean rotateX = xDelta != 0;
            boolean rotateY = yDelta != 0;
            if(rotateX){
                mouseDelta.y = (float)xDelta;
            }
            if(rotateY){
                mouseDelta.x = (float)yDelta;
            }
        }

        previousPosition.x = currentPosition.x;
        previousPosition.y = currentPosition.y;
    }

    public static boolean isRbDown() {
        return rbDown;
    }

    public static boolean isLbDown() {
        return lbDown;
    }

    public static Vector2f getMouseDelta() {
        return mouseDelta;
    }

    public static Vector2d getMousePositionInViewport(){
        double mousePositionX = 1.0 / window.getWidth() * currentPosition.x;
        double mousePositionY = 1.0 / window.getHeight() * currentPosition.y;

        return new Vector2d(mousePositionX, mousePositionY);
    }

    public static Vector2d getMousePositionInPixels(){
        return currentPosition;
    }

    public static void hide(){
        if (GLFW.glfwGetInputMode(windowLong, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED) {
            GLFW.glfwSetInputMode(windowLong, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        }
    }

    public static void show(){
        if (GLFW.glfwGetInputMode(windowLong, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_NORMAL) {
            GLFW.glfwSetInputMode(windowLong, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }
    }
}
