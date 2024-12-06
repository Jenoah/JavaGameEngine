package nl.jenoah.core.loaders;

import nl.jenoah.core.ModelManager;

public class FontLoader {

    private ModelManager modelManager;

    public FontLoader(){
        this.modelManager = ModelManager.getInstance();
    }

    public int load(float[] positions, float[] textureCoords){
        int id = modelManager.createVAO();

        modelManager.storeDataInAttributeList(0, 2, positions);
        modelManager.storeDataInAttributeList(1, 2, textureCoords);

        modelManager.unbind();
        return id;
    }

}
