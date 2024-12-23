package game.entities;

import nl.jenoah.core.*;
import nl.jenoah.core.entity.*;
import nl.jenoah.core.loaders.OBJLoader;
import nl.jenoah.core.shaders.ShaderManager;
import nl.jenoah.core.utils.Constants;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

public class Player extends GameObject {

    private final Camera camera;
    private final WindowManager windowManager;

    private Vector3f moveDelta;
    private float pitch = 0;
    private float yaw = 0;
    private float moveSpeed = Constants.CAMERA_MOVE_SPEED;

    public Player(){
        this.windowManager = WindowManager.getInstance();
        this.camera = new Camera();
        this.camera.setParent(this);
        moveDelta = new Vector3f(0, 0, 0);

        Model pointLightProxyModel = OBJLoader.loadOBJModel("/models/cube.obj");
        //pointLightProxyModel.getMaterial().setAlbedoTexture(null);
        Entity feet = new Entity(pointLightProxyModel, new Vector3f(0, -1f, 0), new Vector3f(0, 0, 0), .1f);
        feet.getModel().setMaterial(new Material(ShaderManager.unlitShader));
        feet.getModel().getMaterial().setAmbientColor(new Vector4f(1, .5f, 1, 1));
        feet.setParent(this);
    }

    @Override
    public void update(MouseInput mouseInput) {
        super.update(mouseInput);
        input();
        move();
        rotate(mouseInput);
    }

    private void input(){
        moveDelta.set(0, 0, 0);

        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_W)){
            moveDelta.z = -1;
        }
        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_S)){
            moveDelta.z = 1;
        }
        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_A)){
            moveDelta.x = -1;
        }
        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_D)){
            moveDelta.x = 1;
        }
        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_SPACE) || windowManager.isKeyPressed(GLFW.GLFW_KEY_E)){
            moveDelta.y = 1;
        }
        if(windowManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || windowManager.isKeyPressed(GLFW.GLFW_KEY_Q)){
            moveDelta.y = -1;
        }
        moveSpeed = windowManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? Constants.CAMERA_MOVE_SPEED * 4f * EngineManager.getDeltaTime() : Constants.CAMERA_MOVE_SPEED * EngineManager.getDeltaTime();
    }

    private void move(){
        Vector3f moveDirection = new Vector3f(moveDelta.x * moveSpeed, moveDelta.y * moveSpeed, moveDelta.z * moveSpeed);
        movePosition(moveDirection);
    }

    private void rotate(MouseInput mouseInput){
        if(mouseInput.isRbDown()){
            Vector2f rotationVector = mouseInput.getMouseDelta();
            moveRotation(rotationVector.x * Constants.MOUSE_SENSITIVITY * EngineManager.getDeltaTime(), rotationVector.y * Constants.MOUSE_SENSITIVITY * EngineManager.getDeltaTime());
            //mouseInput.hide();
        } else {
            // Show the cursor when the right button is not pressed
            //mouseInput.show();
        }
    }

    public void movePosition(Vector3f movePosition){
        if(movePosition.z != 0){
            getPosition().x += (float) Math.sin(yaw) * -1f * movePosition.z;
            getPosition().z += (float) Math.cos(yaw) * movePosition.z;
        }

        if(movePosition.x != 0){
            getPosition().x += (float) Math.sin((yaw - Constants.DEGREES_90_IN_RADIANS)) * -1f * movePosition.x;
            getPosition().z += (float) Math.cos((yaw - Constants.DEGREES_90_IN_RADIANS)) * movePosition.x;
        }

        getPosition().y += movePosition.y;
    }

    public void moveRotation(float xDelta, float yDelta){
        pitch += xDelta;
        yaw += yDelta;

        pitch = Math.clamp(pitch, -Constants.DEGREES_90_IN_RADIANS, Constants.DEGREES_90_IN_RADIANS);

        Quaternionf targetRotation = new Quaternionf().rotateX(pitch).rotateY(yaw).normalize();

        camera.setRotation(targetRotation);
    }

    public Camera getCamera(){
        return camera;
    }
}
