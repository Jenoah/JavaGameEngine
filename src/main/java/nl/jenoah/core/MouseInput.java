package nl.jenoah.core;

import imgui.ImGui;
import nl.jenoah.core.debugging.Debug;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class MouseInput {

    private final Vector2d previousPosition, currentPosition;
    private final Vector2f mouseDelta;

    private boolean lbDown = false;
    private boolean rbDown = false;
    private final long windowLong;
    private final WindowManager window;

    public MouseInput(){
        previousPosition = new Vector2d(-1, -1);
        currentPosition = new Vector2d(0, 0);
        mouseDelta = new Vector2f();
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

    public boolean isRbDown() {
        return rbDown;
    }

    public boolean isLbDown() {
        return lbDown;
    }

    public Vector2f getMouseDelta() {
        return mouseDelta;
    }

    public Vector2d getMousePositionInViewport(){
        double mousePositionX = 1.0 / window.getWidth() * currentPosition.x;
        double mousePositionY = 1.0 / window.getHeight() * currentPosition.y;

        return new Vector2d(mousePositionX, mousePositionY);
    }

    public Vector2d getMousePositionInPixels(){
        return currentPosition;
    }

    public void hide(){
        if (GLFW.glfwGetInputMode(windowLong, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED) {
            GLFW.glfwSetInputMode(windowLong, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        }
    }

    public void show(){
        if (GLFW.glfwGetInputMode(windowLong, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_NORMAL) {
            GLFW.glfwSetInputMode(windowLong, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }
    }
}
