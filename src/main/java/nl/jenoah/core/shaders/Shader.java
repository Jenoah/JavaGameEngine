package nl.jenoah.core.shaders;

import nl.jenoah.core.Camera;
import nl.jenoah.core.WindowManager;
import nl.jenoah.core.debugging.Debug;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.entity.Material;
import nl.jenoah.core.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

public class Shader {
    private final Map<String, Integer> uniforms;
    protected final int programID;

    private int vertexShaderID;
    private int fragmentShaderID;
    protected WindowManager window;

    public Shader() throws Exception{
        programID = GL20.glCreateProgram();
        if(programID == 0){
            throw new Exception("Could not create shader");
        }

        uniforms = new HashMap<>();
        window = WindowManager.getInstance();
    }

    public void init() throws Exception {
        if(vertexShaderID == 0 || fragmentShaderID == 0){
            throw new Exception("Shaders not initialized");
        }

        Debug.Log("Initializing " + getClass().getSimpleName());
        createRequiredUniforms();
    }

    public Shader init(String vertexShader, String fragmentShader) throws Exception {
        createVertexShader(Utils.loadResource(vertexShader));
        createFragmentShader(Utils.loadResource(fragmentShader));
        link();
        init();
        return this;
    }

    public void createRequiredUniforms() throws Exception { }

    public void createVertexShader(String shaderCode) throws Exception{
        vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception{
        fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    public void setTexture(String uniformName, int index){
        int textureLocation = GL30.glGetUniformLocation(programID, uniformName);
        setUniform(textureLocation, index);
    }

    public int createShader(String shaderCode, int shaderType) throws Exception{
        int shaderID = GL20.glCreateShader(shaderType);
        if(shaderID == 0)
            throw new Exception("Error create shader of type " + shaderType);

        GL20.glShaderSource(shaderID, shaderCode);
        GL20.glCompileShader(shaderID);

        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0){
            throw new Exception("Error compiling shader code of type " + shaderType + " with error " + GL30.glGetShaderInfoLog(shaderID, 1024));
        }

        GL20.glAttachShader(programID, shaderID);
        return shaderID;
    }

    public void render(Camera camera){}

    public void prepare(Entity entity, Camera camera){

    }

    public void prepare(){
    }

    public void link() throws Exception{
        GL20.glLinkProgram(programID);
        if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0){
            throw new Exception("Error linking shader code of with error " + GL30.glGetProgramInfoLog(programID, 1024));
        }

        if(vertexShaderID != 0){
            GL20.glDetachShader(programID, vertexShaderID);
        }

        if(fragmentShaderID != 0){
            GL20.glDetachShader(programID, fragmentShaderID);
        }

        //GL20.glValidateProgram(programID);
//        if(GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0){
//            throw new Exception("Unable to validate shader code with error " + GL30.glGetProgramInfoLog(programID, 1024));
//        }
    }

    public void bind(){
        GL20.glUseProgram(programID);
    }

    public void unbind(){
        GL30.glUseProgram(0);
    }

    public void cleanUp(){
        unbind();
        if(programID != 0){
            GL30.glDeleteProgram(programID);
        }
    }

    //Create uniforms

    public void createUniform(String uniformName) throws Exception{
        int uniformLocation = GL20.glGetUniformLocation(programID, uniformName);
        if(uniformLocation < 0){
            throw new Exception("Could not find uniform " + uniformName);
        }

        uniforms.put(uniformName, uniformLocation);
    }

    public void createMaterialUniform(String uniformName) throws Exception{
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }

    //Set uniforms

    public void setUniform(String uniformName, Matrix4f value){
        try(MemoryStack stack = MemoryStack.stackPush()){
            GL20.glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String uniformName, Vector2f value){
        GL20.glUniform2f(uniforms.get(uniformName), value.x, value.y);
    }

    public void setUniform(String uniformName, Vector3f value){
        GL20.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector4f value){
        GL20.glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, boolean value){
        GL20.glUniform1f(uniforms.get(uniformName), value ? 1 : 0);
    }

    public void setUniform(String uniformName, int value){
        GL20.glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(int locationID, int value){
        GL20.glUniform1i(locationID, value);
    }

    public void setUniform(String uniformName, float value){
        GL20.glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Material material){
        setUniform(uniformName + ".ambient", material.getAmbientColor());
        setUniform(uniformName + ".diffuse", material.getDiffuseColor());
        setUniform(uniformName + ".specular", material.getSpecularColor());
        setUniform(uniformName + ".hasTexture", material.hasAlbedoTexture() ? 1 : 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());
    }
}
