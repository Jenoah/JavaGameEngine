package nl.jenoah.core.loaders;

import nl.jenoah.core.ModelManager;
import nl.jenoah.core.entity.Model;

public class RAWLoader {
    public static Model loadModel(float[] vertices, float[] textureCoords, int[] indices, float[] normals){
        int id = ModelManager.createVAO();

        ModelManager.StoreIndicesBuffer(indices);
        ModelManager.storeDataInAttributeList(0, 3, vertices);
        ModelManager.storeDataInAttributeList(1, 2, textureCoords);
        ModelManager.storeDataInAttributeList(2, 3, normals);

        ModelManager.unbind();
        return new Model(id, indices.length);
    }

    public static Model loadModel(float[] vertices, int dimensions){
        int id = ModelManager.createVAO();
        ModelManager.storeDataInAttributeList(0, dimensions, vertices);
        ModelManager.unbind();
        return new Model(id, vertices.length / dimensions);
    }
}
