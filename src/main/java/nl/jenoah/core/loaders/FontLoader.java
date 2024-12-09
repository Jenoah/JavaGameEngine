package nl.jenoah.core.loaders;

import nl.jenoah.core.ModelManager;

public class FontLoader {

    public static int load(float[] positions, float[] textureCoords){
        int id = ModelManager.createVAO();

        ModelManager.storeDataInAttributeList(0, 2, positions);
        ModelManager.storeDataInAttributeList(1, 2, textureCoords);

        ModelManager.unbind();
        return id;
    }

}
