package nl.jenoah.core.entity;

import org.joml.Vector3f;

public class Entity extends GameObject{

    private final Model model;
    private boolean transparent = false;
    private boolean isEnabled = true;

    public Entity(Model model, Vector3f position, Vector3f rotation, float scale) {
        this.model = model;
        setPosition(position);
        setRotation(rotation);
        setScale(scale);
    }

    public Entity(Model model, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.model = model;
        setPosition(position);
        setRotation(rotation);
        setScale(scale.x, scale.y, scale.z);
    }

    public Entity(Model model, Vector3f position, Vector3f rotation, float scale, boolean transparent) {
        this(model, position, rotation, scale);
        setTransparency(transparent);
    }

    public void setTransparency(boolean transparency){
        this.transparent = transparency;
    }

    public Model getModel() {
        return model;
    }

    public boolean isTransparent(){
        return transparent;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
