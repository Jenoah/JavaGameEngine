package nl.jenoah.core.entity;

import nl.jenoah.core.MouseInput;
import nl.jenoah.core.utils.Calculus;
import org.joml.*;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private Vector3f localPosition = new Vector3f();
    private Quaternionf localRotation = new Quaternionf();
    private Vector3f scale = new Vector3f(1f);

    private List<GameObject> children;
    private GameObject parent;

    public GameObject() {
        this.children = new ArrayList<>();
    }

    public Vector3f getPosition() {
        if(parent != null){
            return Calculus.addVectors(parent.getPosition(), localPosition);
        }

        return localPosition;
    }

    public Vector3f getLocalPosition(){
        return  localPosition;
    }

    public void setPosition(Vector3f position) {
        this.localPosition = position;
    }

    public void setPosition(float x, float y) {
        this.localPosition = new Vector3f(x, y, 0);
    }

    public void setPosition(float x, float y, float z) {
        this.localPosition = new Vector3f(x, y, z);
    }

    public void addPosition(Vector3f position){
        this.localPosition = Calculus.addVectors(this.localPosition, position);
    }

    public Quaternionf getRotation() {
        if(parent != null){
            Quaternionf outputQuaternion = new Quaternionf();
            outputQuaternion.set(parent.getRotation()).mul(getLocalRotation());
            return outputQuaternion;
        }

        return localRotation;
    }

    public Vector3f getEulerAngles(){
        Vector3f eulerAngles = new Vector3f();
        getRotation().getEulerAnglesXYZ(eulerAngles);

        eulerAngles.x = (float) Math.toDegrees(eulerAngles.x);
        eulerAngles.y = (float) Math.toDegrees(eulerAngles.y);
        eulerAngles.z = (float) Math.toDegrees(eulerAngles.z);

        return eulerAngles;
    }

    public Vector3f getLocalEulerAngles(){
        Vector3f eulerAngles = new Vector3f();
        localRotation.getEulerAnglesXYZ(eulerAngles);

        eulerAngles.x = (float) Math.toDegrees(eulerAngles.x);
        eulerAngles.y = (float) Math.toDegrees(eulerAngles.y);
        eulerAngles.z = (float) Math.toDegrees(eulerAngles.z);

        return eulerAngles;
    }

    public Quaternionf getLocalRotation(){
        return  localRotation;
    }

    public void setRotation(Quaternionf rotation){
        this.localRotation = rotation;
    }

    public void setRotation(Vector3f rotation) {
        rotation.x = (float) Math.toRadians(rotation.x);
        rotation.y = (float) Math.toRadians(rotation.y);
        rotation.z = (float) Math.toRadians(rotation.z);
        this.localRotation = new Quaternionf().rotateXYZ(rotation.x, rotation.y, rotation.z).normalize();
    }

    public void addRotation(Vector3f rotation){
        rotation.x = (float) Math.toRadians(rotation.x);
        rotation.y = (float) Math.toRadians(rotation.y);
        rotation.z = (float) Math.toRadians(rotation.z);

        Quaternionf additionalRotation = new Quaternionf().identity().rotateXYZ(
                rotation.x,
                rotation.y,
                rotation.z
        );

        localRotation.mul(additionalRotation).normalize();
    }

    public void addRotation(Quaternionf rotation){
        localRotation.mul(rotation).normalize();
    }

    public void lookAt(Vector3f target){
        Vector3f currentPosition = getPosition();
        Vector3f forward = new Vector3f(currentPosition).sub(target).normalize();

        if (forward.lengthSquared() < 1e-6) {
            return; // Avoid calculations if at the target position
        }

        Vector3f referenceDirection = new Vector3f(0, 0, -1).normalize();

        Quaternionf quaternion = new Quaternionf();
        quaternion.rotationTo(referenceDirection, forward);

        setRotation(quaternion);
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = new Vector3f(scale);
    }

    public void setScale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
    }

    public void setScale(float x, float y) {
        this.scale = new Vector3f(x, y, 0);
    }

    public void addChild(GameObject child){
        if(!children.contains(child)) {
            children.add(child);
        }
    }

    public GameObject getChild(int childIndex){
        return children.get(childIndex);
    }

    public List<GameObject> getChildren(){
        return children;
    }

    public void setParent(GameObject parent){
        if(this.parent != null){
            this.parent.children.remove(this);
        }

        this.parent = parent;
        if(parent != null && !parent.children.contains(this)){
            parent.addChild(this);
        }
    }

    public GameObject getParent(){
        return this.parent;
    }

    public void update(MouseInput mouseInput){
    }

    public String ToString(){
        return this.getClass().getName();
    }
}
