package game.components;

import nl.jenoah.core.EngineManager;
import nl.jenoah.core.MouseInput;
import nl.jenoah.core.WindowManager;
import nl.jenoah.core.components.Component;
import nl.jenoah.core.entity.GameObject;
import nl.jenoah.core.utils.Constants;
import nl.jenoah.core.utils.ObjectPool;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class PlayerMovement extends Component {
    private final WindowManager windowManager;
    private final GameObject cameraObject;

    private final Vector3f moveDelta = new Vector3f();
    private float pitch = 0;
    private float yaw = 0;

    public PlayerMovement(GameObject cameraObject) {
        this.windowManager = WindowManager.getInstance();
        this.cameraObject = cameraObject;
    }

    @Override
    public void update() {
        super.update();

        move();
    }

    public void input(MouseInput mouseInput) {
        if (!mouseInput.isRbDown()) return;
        rotate(mouseInput.getMouseDelta());
    }

    private void move() {
        moveDelta.set(0, 0, 0);
        float moveSpeed = windowManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ?
                Constants.CAMERA_MOVE_SPEED * EngineManager.getDeltaTime() * 4f :
                Constants.CAMERA_MOVE_SPEED * EngineManager.getDeltaTime();

        // Forwards / Backwards
        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_W)) {
            moveDelta.z = -1;
        } else if (windowManager.isKeyPressed(GLFW.GLFW_KEY_S)) {
            moveDelta.z = 1;
        }
        // Left / right
        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_A)) {
            moveDelta.x = -1;
        } else if (windowManager.isKeyPressed(GLFW.GLFW_KEY_D)) {
            moveDelta.x = 1;
        }
        // Up / down
        if (windowManager.isKeyPressed(GLFW.GLFW_KEY_SPACE) || windowManager.isKeyPressed(GLFW.GLFW_KEY_E)) {
            moveDelta.y = 1;
        } else if (windowManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || windowManager.isKeyPressed(GLFW.GLFW_KEY_Q)) {
            moveDelta.y = -1;
        }

        if (moveDelta.length() > 0) moveDelta.normalize(moveSpeed);

        Vector3f targetPosition = ObjectPool.VECTOR3F_POOL.obtain().set(0);

        if (moveDelta.z != 0) {
            targetPosition.x += (float) Math.sin(yaw) * -1f * moveDelta.z;
            targetPosition.z += (float) Math.cos(yaw) * moveDelta.z;
        }

        if (moveDelta.x != 0) {
            targetPosition.x += (float) Math.sin((yaw - Constants.DEGREES_90_IN_RADIANS)) * -1f * moveDelta.x;
            targetPosition.z += (float) Math.cos((yaw - Constants.DEGREES_90_IN_RADIANS)) * moveDelta.x;
        }

        targetPosition.y += moveDelta.y;

        root.translateLocal(targetPosition);

        ObjectPool.VECTOR3F_POOL.free(targetPosition);
    }

    public void rotate(Vector2f mouseDelta) {
        yaw += mouseDelta.x * Constants.MOUSE_SENSITIVITY * EngineManager.getDeltaTime();
        pitch += mouseDelta.y * Constants.MOUSE_SENSITIVITY * EngineManager.getDeltaTime();

        pitch = Math.clamp(pitch, -Constants.DEGREES_90_IN_RADIANS, Constants.DEGREES_90_IN_RADIANS);

        Quaternionf targetRotation = ObjectPool.QUATERNIONF_OBJECT_POOL.obtain().identity().rotateY(yaw).rotateX(pitch).normalize();

        cameraObject.setRotation(targetRotation);

        ObjectPool.QUATERNIONF_OBJECT_POOL.free(targetRotation);
    }
}
