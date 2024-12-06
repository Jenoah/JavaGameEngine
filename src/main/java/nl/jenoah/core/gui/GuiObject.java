package nl.jenoah.core.gui;

import nl.jenoah.core.entity.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class GuiObject extends GameObject {

    private final int texture;

    private Vector4f color = new Vector4f(1f);

    public GuiObject(int texture, Vector2f position, Vector2f scale) {
        super();
        this.texture = texture;
        this.setPosition(position.x, position.y);
        this.setScale(scale.x, scale.y);
    }

    public void setColor(Vector4f color){
        this.color = color;
    }

    public void setColor(Vector3f color){
        this.color = new Vector4f(color, 1.0f);
    }

    public Vector4f getColor() {
        return color;
    }

    public int getTexture(){
        return texture;
    }
}
