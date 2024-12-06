package nl.jenoah.core.loaders;

import nl.jenoah.core.ModelManager;
import nl.jenoah.core.entity.Model;

public class PrimitiveLoader {
    private ModelManager modelManager;

    public PrimitiveLoader(){
        this.modelManager = ModelManager.getInstance();
    }

    public Model getQuad(){
        float[] vertices = new float[]{
                -0.5f,  -0.5f, 0.0f, // Top-left
                0.5f,  -0.5f, 0.0f, // Top-right
                0.5f, 0.5f, 0.0f, // Bottom-right
                -0.5f, 0.5f, 0.0f  // Bottom-left
        };
        float[] textureCoords = new float[]{
                0.0f, 1.0f, // Top-left
                1.0f, 1.0f, // Top-right
                1.0f, 0.0f, // Bottom-right
                0.0f, 0.0f  // Bottom-left
        };
        int[] indices = new int[]{
                0, 1, 2, // First triangle (top-right)
                0, 2, 3  // Second triangle (bottom-left)
        };
        float[] normals = new float[]{
                0.0f, 0.0f, 1.0f, // Top-left
                0.0f, 0.0f, 1.0f, // Top-right
                0.0f, 0.0f, 1.0f, // Bottom-right
                0.0f, 0.0f, 1.0f  // Bottom-left
        };

        return loadModel(vertices, textureCoords, indices, normals);
    }

    public Model getCube() {
        // Define the vertices for the cube
        float[] vertices = new float[]{
                // Front face
                -0.5f, -0.5f,  0.5f,  // Bottom-left
                0.5f, -0.5f,  0.5f,  // Bottom-right
                0.5f,  0.5f,  0.5f,  // Top-right
                -0.5f,  0.5f,  0.5f,  // Top-left

                // Back face
                -0.5f, -0.5f, -0.5f,  // Bottom-left
                -0.5f,  0.5f, -0.5f,  // Top-left
                0.5f,  0.5f, -0.5f,  // Top-right
                0.5f, -0.5f, -0.5f,  // Bottom-right

                // Left face
                -0.5f, -0.5f, -0.5f,  // Bottom-left
                -0.5f, -0.5f,  0.5f,  // Bottom-right
                -0.5f,  0.5f,  0.5f,  // Top-right
                -0.5f,  0.5f, -0.5f,  // Top-left

                // Right face
                0.5f, -0.5f, -0.5f,  // Bottom-left
                0.5f,  0.5f, -0.5f,  // Top-left
                0.5f,  0.5f,  0.5f,  // Top-right
                0.5f, -0.5f,  0.5f,  // Bottom-right

                // Top face
                -0.5f,  0.5f, -0.5f,  // Back-left
                0.5f,  0.5f, -0.5f,  // Back-right
                0.5f,  0.5f,  0.5f,  // Front-right
                -0.5f,  0.5f,  0.5f,  // Front-left

                // Bottom face
                -0.5f, -0.5f, -0.5f,  // Back-left
                0.5f, -0.5f, -0.5f,  // Back-right
                0.5f, -0.5f,  0.5f,  // Front-right
                -0.5f, -0.5f,  0.5f   // Front-left
        };

        // Define the texture coordinates for each face
        float[] textureCoords = new float[]{
                // Front face
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,

                // Back face
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                // Left face
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,

                // Right face
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,

                // Top face
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,

                // Bottom face
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f
        };

        // Define the indices for the cube's triangles
        int[] indices = new int[]{
                // Front face
                0, 1, 2,
                0, 2, 3,

                // Back face
                4, 5, 6,
                4, 6, 7,

                // Left face
                8, 9, 10,
                8, 10, 11,

                // Right face
                12, 13, 14,
                12, 14, 15,

                // Top face
                16, 19, 18,
                16, 18, 17,

                // Bottom face
                20, 21, 22,
                20, 22, 23
        };

        // Define normals for each face
        float[] normals = new float[]{
                // Front face normals
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,

                // Back face normals
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,

                // Left face normals
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,

                // Right face normals
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,

                // Top face normals
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                // Bottom face normals
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f
        };

        return loadModel(vertices, textureCoords, indices, normals);
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

    public Model loadModel(float[] vertices){
        int id = modelManager.createVAO();

        modelManager.storeDataInAttributeList(0, 3, vertices);

        modelManager.unbind();
        return new Model(id, vertices.length);
    }
}
