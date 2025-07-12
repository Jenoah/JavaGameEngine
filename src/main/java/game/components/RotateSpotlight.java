package game.components;

import nl.jenoah.core.EngineManager;
import nl.jenoah.core.components.Component;
import nl.jenoah.core.lighting.SpotLight;
import nl.jenoah.core.utils.ObjectPool;
import org.joml.Vector3f;

public class RotateSpotlight extends Component {

    private final SpotLight spotLight;
    private Vector3f rotation = new Vector3f(0);

    public RotateSpotlight(SpotLight spotLight){
        this.spotLight = spotLight;
    }

    public RotateSpotlight(SpotLight spotLight, Vector3f rotation){
        this.spotLight = spotLight;
        this.rotation = rotation;
    }

    public void setRotation(Vector3f rotation){
        this.rotation = rotation;
    }

    @Override
    public void update() {
        Vector3f addedRotation = ObjectPool.VECTOR3F_POOL.obtain().set(rotation);
        addedRotation.mul(EngineManager.getDeltaTime());
        root.addRotation(addedRotation);
        ObjectPool.VECTOR3F_POOL.free(addedRotation);
    }
}
