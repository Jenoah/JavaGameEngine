package nl.framegengine.core.entity;

import nl.framegengine.core.WindowManager;
import nl.framegengine.core.rendering.FrustumPlane;
import nl.framegengine.core.rendering.RenderManager;
import nl.framegengine.core.utils.AABB;
import nl.framegengine.core.utils.ObjectPool;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera extends GameObject {

    public static Camera mainCamera = null;

    private final Matrix4f viewProjectionMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();
    private final WindowManager windowManager;
    private FrustumPlane[] frustumPlanes = new FrustumPlane[6];

    public Camera() {
        super();

        if(mainCamera == null) mainCamera = this;
        windowManager = WindowManager.getInstance();

        setPosition(new Vector3f(0, 0, 0));

        updateViewFrustum();
        callUpdate();
    }

    public Camera(Vector3f position, Vector3f rotation) {
        super();

        windowManager = WindowManager.getInstance();

        setPosition(position);
        setRotation(rotation);

        updateViewFrustum();
        callUpdate();
    }

    public Camera(Vector3f position, Quaternionf rotation) {
        super();

        windowManager = WindowManager.getInstance();

        setPosition(position);
        setRotation(rotation);

        updateViewFrustum();
        callUpdate();
    }

    public void updateViewFrustum(){
        Vector3f normal = new Vector3f();

        // Left plane
        normal.set(viewProjectionMatrix.m03() + viewProjectionMatrix.m00(),
                viewProjectionMatrix.m13() + viewProjectionMatrix.m10(),
                viewProjectionMatrix.m23() + viewProjectionMatrix.m20());
        float d = viewProjectionMatrix.m33() + viewProjectionMatrix.m30();
        frustumPlanes[0] = new FrustumPlane(normal, d);
        frustumPlanes[0].normalize();

        // Right plane
        normal = new Vector3f(viewProjectionMatrix.m03() - viewProjectionMatrix.m00(),
                viewProjectionMatrix.m13() - viewProjectionMatrix.m10(),
                viewProjectionMatrix.m23() - viewProjectionMatrix.m20());
        d = viewProjectionMatrix.m33() - viewProjectionMatrix.m30();
        frustumPlanes[1] = new FrustumPlane(normal, d);
        frustumPlanes[1].normalize();

        // Bottom plane
        normal = new Vector3f(viewProjectionMatrix.m03() + viewProjectionMatrix.m01(),
                viewProjectionMatrix.m13() + viewProjectionMatrix.m11(),
                viewProjectionMatrix.m23() + viewProjectionMatrix.m21());
        d = viewProjectionMatrix.m33() + viewProjectionMatrix.m31();
        frustumPlanes[2] = new FrustumPlane(normal, d);
        frustumPlanes[2].normalize();

        // Top plane
        normal = new Vector3f(viewProjectionMatrix.m03() - viewProjectionMatrix.m01(),
                viewProjectionMatrix.m13() - viewProjectionMatrix.m11(),
                viewProjectionMatrix.m23() - viewProjectionMatrix.m21());
        d = viewProjectionMatrix.m33() - viewProjectionMatrix.m31();
        frustumPlanes[3] = new FrustumPlane(normal, d);
        frustumPlanes[3].normalize();

        // Near plane
        normal = new Vector3f(viewProjectionMatrix.m03() + viewProjectionMatrix.m02(),
                viewProjectionMatrix.m13() + viewProjectionMatrix.m12(),
                viewProjectionMatrix.m23() + viewProjectionMatrix.m22());
        d = viewProjectionMatrix.m33() + viewProjectionMatrix.m32();
        frustumPlanes[4] = new FrustumPlane(normal, d);
        frustumPlanes[4].normalize();

        // Far plane
        normal = new Vector3f(viewProjectionMatrix.m03() - viewProjectionMatrix.m02(),
                viewProjectionMatrix.m13() - viewProjectionMatrix.m12(),
                viewProjectionMatrix.m23() - viewProjectionMatrix.m22());
        d = viewProjectionMatrix.m33() - viewProjectionMatrix.m32();
        frustumPlanes[5] = new FrustumPlane(normal, d);
        frustumPlanes[5].normalize();
    }

    public boolean isInFrustumSphere(GameObject object){
        boolean isInFrustum = true;
        for (FrustumPlane plane : frustumPlanes) {
            if (plane.isSphereOutside(object.getPosition(), object.getRadius())) {
                isInFrustum = false;
                break;
            }
        }

        return isInFrustum;
    }

    public boolean isInFrustumAABB(GameObject object) {
        AABB worldAABB = new AABB(object.getAabb()).offset(object.getPosition());

        Vector3f positiveCorner = ObjectPool.VECTOR3F_POOL.obtain().set(0,0,0);

        for (FrustumPlane plane : frustumPlanes) {
            positiveCorner = ObjectPool.VECTOR3F_POOL.obtain().set(
                    plane.normal.x > 0 ? worldAABB.max.x : worldAABB.min.x,
                    plane.normal.y > 0 ? worldAABB.max.y : worldAABB.min.y,
                    plane.normal.z > 0 ? worldAABB.max.z : worldAABB.min.z
            );

            if (plane.getDistanceTo(positiveCorner) < 0) {
                ObjectPool.VECTOR3F_POOL.free(positiveCorner);
                return false;
            }
        }

        ObjectPool.VECTOR3F_POOL.free(positiveCorner);
        return true;
    }

    public final Matrix4f getViewProjectionMatrix(){
        return viewProjectionMatrix;
    }

    public final Matrix4f updateViewProjectionMatrix(){
        this.viewProjectionMatrix.set(windowManager.getProjectionMatrix()).mul(getViewMatrix());
        return viewProjectionMatrix;
    }

    public final Matrix4f getViewMatrix(){
        return this.viewMatrix;
    }

    public final Matrix4f updateViewMatrix(){
        Vector3f currentPosition = getPosition();

        this.viewMatrix.identity()
                .rotate(getRotation())
                .translate(-currentPosition.x, -currentPosition.y, -currentPosition.z);

        return this.viewMatrix;
    }

    public void setAsMain(){
        mainCamera = this;
        if(RenderManager.getInstance() != null) RenderManager.getInstance().setRenderCamera(mainCamera);
    }

    @Override
    protected void OnUpdateTransform() {
        super.OnUpdateTransform();
        if(!willUpdate) return;
        updateViewMatrix();
        updateViewProjectionMatrix();
        updateViewFrustum();
        willUpdate = false;
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        mainCamera = null;
    }
}
