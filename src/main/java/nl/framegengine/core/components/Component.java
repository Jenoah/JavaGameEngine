package nl.framegengine.core.components;

import nl.framegengine.core.entity.GameObject;

public class Component {
    protected GameObject root = null;
    protected boolean hasInitiated = false;

    //TODO: MAKE isEnabled LOOK AT THE isEnabled STATE OF THE root
    public boolean isEnabled = true;

    public void initiate(){
        if(hasInitiated) return;
        hasInitiated = true;
    }
    public void update(){}

    public GameObject getRoot(){
        return root;
    }

    public Component setRoot(GameObject root){
        this.root = root;
        return this;
    }

    public Component clone() {
        return new Component().setRoot(this.getRoot());
    }

    public void enable(){
        isEnabled = true;
    }

    public void disable(){
        isEnabled = false;
    }

    public final boolean getEnabled(){
        return isEnabled;
    }

    public void cleanUp(){ }
}
