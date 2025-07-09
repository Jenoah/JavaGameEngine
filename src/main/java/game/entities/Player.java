package game.entities;

import game.components.PlayerMovement;
import nl.jenoah.core.*;
import nl.jenoah.core.components.RenderComponent;
import nl.jenoah.core.entity.*;
import nl.jenoah.core.loaders.PrimitiveLoader;
import nl.jenoah.core.shaders.ShaderManager;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Player extends GameObject {

    private final Camera camera;

    private final PlayerMovement playerMovement;

    public Player(){
        setName("Player");

        this.camera = new Camera();
        this.camera.setParent(this);

        this.camera.setName("Camera");

        GameObject feet = new GameObject("Feet").setPosition(new Vector3f(0, -1f, 0)).setScale(0.1f);
        Material feetMaterial = new Material(ShaderManager.unlitShader).setAmbientColor(new Vector4f(1, .5f, 1, 1));
        feet.addComponent(new RenderComponent(PrimitiveLoader.getCube().getMesh(), feetMaterial));

        playerMovement = new PlayerMovement(this.camera);

        addComponent(playerMovement);
        playerMovement.initiate();

        feet.setParent(this);
    }

    @Override
    public void update(MouseInput mouseInput) {
        super.update(mouseInput);
        playerMovement.input(mouseInput);
        playerMovement.update();

        camera.updateViewFrustum();
    }

    public Camera getCamera(){
        return camera;
    }
}
