package nl.jenoah.core.loaders;

import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class TextureLoader {

    private final List<Integer> textures = new ArrayList<>();
    private boolean flipTexture = true;
    private boolean pointFilter = false;

    public int loadTexture(String fileName){
        ByteBuffer imageBuffer;
        int width, height, alphaFormat;
        IntBuffer comp;

        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            comp = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(flipTexture);
            imageBuffer = STBImage.stbi_load(fileName, w, h, comp, 0);
            if(imageBuffer == null){
                throw new Exception("Image file " + fileName + " could not be loaded because " + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        int id = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        int format;
        if (comp.get() == 3) {
            if ((width & 3) != 0) {
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 2 - (width & 1));
            }
            format = GL11.GL_RGB;
            alphaFormat = GL11.GL_RGB;
        } else {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

            format = GL30.GL_SRGB_ALPHA;
            alphaFormat = GL11.GL_RGBA;
        }

        //GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_SRGB_ALPHA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBuffer);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, width, height, 0, alphaFormat, GL11.GL_UNSIGNED_BYTE, imageBuffer);


        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE); // or GL_CLAMP_TO_EDGE
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_REPEAT); // or GL_CLAMP_TO_EDGE

        if(pointFilter) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }else{
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        }
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0f);

        STBImage.stbi_image_free(imageBuffer);

        textures.add(id);
        return id;
    }

    public int loadTexture(String fileName, boolean pointFilter){
        this.pointFilter = pointFilter;
        return loadTexture(fileName);
    }

    public int loadTexture(String fileName, boolean pointFilter, boolean flipTexture){
        this.pointFilter = pointFilter;
        this.flipTexture = flipTexture;
        return loadTexture(fileName);
    }

    public int loadCubeMapTexture(String[] fileNames){
        int textureID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);

        for(int i = 0; i < fileNames.length; i++){
            TextureData data = getTextureData(fileNames[i]);
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.buffer);
        }
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        textures.add(textureID);
        return textureID;
    }

    private TextureData getTextureData(String fileName){
            ByteBuffer imageBuffer;
            int width, height, alphaFormat;
            IntBuffer comp;

            try(MemoryStack stack = MemoryStack.stackPush()){
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                comp = stack.mallocInt(1);

                stbi_set_flip_vertically_on_load(false);
                imageBuffer = STBImage.stbi_load(fileName, w, h, comp, 4);
                if(imageBuffer == null){
                    throw new Exception("Image file " + fileName + " could not be loaded because " + STBImage.stbi_failure_reason());
                }

                width = w.get();
                height = h.get();

                return new TextureData(imageBuffer, width, height);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }


    public void cleanUp(){
        for(int texture: textures){
            GL11.glDeleteTextures(texture);
        }
    }

    private class TextureData{
        private int width, height;
        private ByteBuffer buffer;

        public TextureData(ByteBuffer buffer, int width, int height){
            this.height = height;
            this.width = width;
            this.buffer = buffer;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public ByteBuffer getBuffer() {
            return buffer;
        }
    }

}
