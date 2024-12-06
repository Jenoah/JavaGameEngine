package nl.jenoah.core.loaders;

import nl.jenoah.core.ModelManager;
import nl.jenoah.core.entity.Model;

public class GUILoader {

    private ModelManager modelManager;

    public GUILoader(){
        this.modelManager = ModelManager.getInstance();
    }

    public Model load(float[] position){
        int id = modelManager.createVAO();

        modelManager.storeDataInAttributeList(0, 2, position);

        modelManager.unbind();
        return new Model(id, position.length);
    }
}
