package nl.jenoah.core.loaders;

import nl.jenoah.core.ModelManager;
import nl.jenoah.core.entity.Model;

public class GUILoader {

    public static Model load(float[] position){
        int id = ModelManager.createVAO();

        ModelManager.storeDataInAttributeList(0, 2, position);

        ModelManager.unbind();
        return new Model(id, position.length);
    }
}
