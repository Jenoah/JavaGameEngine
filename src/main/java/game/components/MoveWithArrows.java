package game.components;

import nl.jenoah.core.EngineManager;
import nl.jenoah.core.WindowManager;
import nl.jenoah.core.components.Component;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class MoveWithArrows extends Component {

    private final WindowManager windowManager;

    public MoveWithArrows() {
        this.windowManager = WindowManager.getInstance();
    }

    @Override
    public void update() {
        super.update();

        float moveSpeed = 5f * EngineManager.getDeltaTime();

        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_UP)){
            root.translateLocal(new Vector3f(0, 0, -moveSpeed));
        }
        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_DOWN)){
            root.translateLocal(new Vector3f(0, 0, moveSpeed));
        }
        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_LEFT)){
            root.translateLocal(new Vector3f(-moveSpeed, 0, 0));
        }
        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_RIGHT)){
            root.translateLocal(new Vector3f(moveSpeed, 0, 0));
        }
    }
}
