package nl.jenoah.core.entity;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Entity extends GameObject{

    private final Model model;
    private boolean transparent = false;


    public Entity(Model model, Vector3f position, Vector3f rotation, float scale) {
        this.model = model;
        setPosition(position);
        setRotation(rotation);
        setScale(scale);
    }

    public Entity(Model model, Vector3f position, Quaternionf rotation, float scale) {
        this.model = model;
        setPosition(position);
        setRotation(rotation);
        setScale(scale);
    }

    public Entity(Entity entity) {
        this(entity.getModel(), entity.getPosition(), entity.getEulerAngles(), entity.getScale());
        transparent = entity.transparent;
        isEnabled = entity.isEnabled;
    }

    public Entity(Entity entity, Vector3f position, Vector3f rotation, float scale) {
        this(entity.getModel(), position, rotation, scale);
        transparent = entity.transparent;
        isEnabled = entity.isEnabled;
    }

    public Entity(Entity entity, Vector3f position, Quaternionf rotation, float scale) {
        this(entity.getModel(), position, rotation, scale);
        transparent = entity.transparent;
        isEnabled = entity.isEnabled;
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
}
