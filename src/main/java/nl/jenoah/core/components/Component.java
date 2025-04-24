package nl.jenoah.core.components;

import nl.jenoah.core.entity.GameObject;

public class Component {
    private GameObject root = null;
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
}
