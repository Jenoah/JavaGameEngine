package nl.jenoah.core.loaders;

import nl.jenoah.core.ModelManager;
import nl.jenoah.core.entity.Model;

public class RAWLoader {
    private ModelManager modelManager;

    public RAWLoader(){
        this.modelManager = ModelManager.getInstance();
    }

    public Model loadModel(float[] vertices, float[] textureCoords, int[] indices, float[] normals){
        int id = modelManager.createVAO();

        modelManager.StoreIndicesBuffer(indices);
        modelManager.storeDataInAttributeList(0, 3, vertices);
        modelManager.storeDataInAttributeList(1, 2, textureCoords);
        modelManager.storeDataInAttributeList(2, 3, normals);

        modelManager.unbind();
        return new Model(id, indices.length);
    }

    public Model loadModel(float[] vertices, int dimensions){
        int id = modelManager.createVAO();
        modelManager.storeDataInAttributeList(0, dimensions, vertices);
        modelManager.unbind();
        return new Model(id, vertices.length / dimensions);
    }
}
