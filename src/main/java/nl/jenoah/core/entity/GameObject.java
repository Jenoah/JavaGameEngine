package nl.jenoah.core.entity;

import nl.jenoah.core.MouseInput;
import nl.jenoah.core.components.Component;
import nl.jenoah.core.debugging.Debug;
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
    protected boolean isEnabled = true;

    private final List<Component> components = new ArrayList<>();

    public GameObject() {
        this.children = new ArrayList<>();
    }

    public void initiate(){
        for (Component component : components){
            component.initiate();
        }
    }

    public Vector3f getPosition() {
        if(parent != null){
            return Calculus.addVectors(parent.getPosition(), localPosition);
        }

        return localPosition;
    }

    public Vector3f getLocalPosition(){
        return localPosition;
    }

    public GameObject setPosition(Vector3f position) {
        this.localPosition = position;
        return this;
    }

    public GameObject setPosition(float x, float y) {
        this.localPosition = new Vector3f(x, y, 0);
        return this;
    }

    public GameObject setPosition(float x, float y, float z) {
        this.localPosition = new Vector3f(x, y, z);
        return this;
    }

    public GameObject addPosition(Vector3f position){
        this.localPosition = Calculus.addVectors(this.localPosition, position);
        return this;
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

    public GameObject setRotation(Quaternionf rotation){
        this.localRotation = rotation;
        return this;
    }

    public GameObject setRotation(Vector3f rotation) {
        rotation.x = (float) Math.toRadians(rotation.x);
        rotation.y = (float) Math.toRadians(rotation.y);
        rotation.z = (float) Math.toRadians(rotation.z);
        this.localRotation = new Quaternionf().rotateXYZ(rotation.x, rotation.y, rotation.z).normalize();
        return this;
    }

    public GameObject addRotation(Vector3f rotation){
        rotation.x = (float) Math.toRadians(rotation.x);
        rotation.y = (float) Math.toRadians(rotation.y);
        rotation.z = (float) Math.toRadians(rotation.z);

        Quaternionf additionalRotation = new Quaternionf().identity().rotateXYZ(
                rotation.x,
                rotation.y,
                rotation.z
        );

        localRotation.mul(additionalRotation).normalize();
        return this;
    }

    public GameObject addRotation(Quaternionf rotation){
        localRotation.mul(rotation).normalize();
        return this;
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

    public GameObject setScale(float scale) {
        this.scale = new Vector3f(scale);
        return this;
    }

    public GameObject setScale(Vector3f scale) {
        this.scale = scale;
        return this;
    }

    public GameObject setScale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        return this;
    }

    public GameObject setScale(float x, float y) {
        this.scale = new Vector3f(x, y, 0);
        return this;
    }

    public GameObject addChild(GameObject child){
        if(!children.contains(child)) {
            children.add(child);
        }
        return this;
    }

    public GameObject getChild(int childIndex){
        return children.get(childIndex);
    }

    public List<GameObject> getChildren(){
        return children;
    }

    public GameObject setParent(GameObject parent){
        if(this.parent != null){
            this.parent.children.remove(this);
        }

        this.parent = parent;
        if(parent != null && !parent.children.contains(this)){
            parent.addChild(this);
        }
        return this;
    }

    public GameObject getParent(){
        return this.parent;
    }

    public void update(MouseInput mouseInput){
        if(!isEnabled || components.isEmpty()) return;
        for(Component component : components){ component.update(); }
    }

    public boolean isEnabled() {
        if(parent != null){
            return isEnabled && parent.isEnabled();
        }
        return isEnabled;
    }

    public GameObject setEnabled(boolean enabled) {
        isEnabled = enabled;
        return this;
    }

    public List<Component> getComponents(){
        return components;
    }

    public <C extends Component> C getComponent(Class<C> component){
        for (Component c : components) {
            if (c.getClass() == component)
                return (C) c;
        }

        return null;
    }

    public Component addComponent(Component component){
        if(this.components.contains(component)){
            Debug.Log("GameObject already contains component of type " + component.getClass().getSimpleName());
            return null;
        }

        component.setRoot(this);
        this.components.add(component);
        return component;
    }

    public String ToString(){
        return this.getClass().getSimpleName();
    }
}
