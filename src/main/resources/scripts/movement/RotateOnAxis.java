package nl.framegengine.customScripts;

import nl.jenoah.core.EngineManager;
import nl.jenoah.core.components.Component;
import nl.jenoah.core.utils.Constants;
import nl.jenoah.core.utils.ObjectPool;
import org.joml.Vector3f;

public class RotateOnAxis extends Component {

    private Vector3f rotationAxis = Constants.VECTOR3_UP;
    private float rotationSpeed = 50f;

    @Override
    public void update() {
        super.update();

        Vector3f addedRotation = ObjectPool.VECTOR3F_POOL.obtain().set(rotationAxis);
        addedRotation.mul(EngineManager.getDeltaTime() * rotationSpeed);
        root.addRotation(addedRotation);
        ObjectPool.VECTOR3F_POOL.free(addedRotation);
    }
}