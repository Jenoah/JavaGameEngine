package nl.jenoah.core.shaders;

import nl.jenoah.core.Camera;
import nl.jenoah.core.entity.Entity;
import nl.jenoah.core.entity.SceneManager;
import nl.jenoah.core.lighting.DirectionalLight;
import nl.jenoah.core.lighting.PointLight;
import nl.jenoah.core.lighting.SpotLight;
import nl.jenoah.core.utils.Constants;
import nl.jenoah.core.utils.Transformation;
import nl.jenoah.core.utils.Utils;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.glDepthMask;

public class SimpleLitShader extends Shader {

    private PointLight[] pointLights = new PointLight[0];
    private SpotLight[] spotLights = new SpotLight[0];
    private DirectionalLight directionalLight;

    public SimpleLitShader() throws Exception {
        super();
    }

    public void init() throws Exception {
        createVertexShader(Utils.loadResource("/shaders/lit/simpleLit/vertex.vs"));
        createFragmentShader(Utils.loadResource("/shaders/lit/simpleLit/fragment.fs"));
        link();
        super.init();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        super.createRequiredUniforms();

        createMaterialUniform("material");
        createUniform("textureSampler");
        createUniform("modelMatrix");
        createUniform("viewMatrix");
        createUniform("projectionMatrix");
        createUniform("fogColor");
        createUniform("fogDensity");
        createUniform("fogGradient");

        createUniform("ambientColor");
        createUniform("specularPower");
        createUniform("viewPosition");
        createDirectionalLightUniform("directionalLight");
        createPointLightArrayUniform("pointLights", 5);
        createSpotLightArrayUniform("spotLights", 5);
    }

    @Override
    public void prepare(Entity entity, Camera camera){
        glDepthMask(true);

        Matrix4f modelMatrix = Transformation.getModelMatrix(entity);
        Matrix4f viewMatrix = Transformation.getViewMatrix(camera);

        Shader shader = entity.getModel().getMaterial().getShader();
        shader.setUniform("modelMatrix", modelMatrix);
        shader.setUniform("textureSampler", 0);
        shader.setUniform("viewMatrix", viewMatrix);
        shader.setUniform("projectionMatrix", window.getProjectionMatrix());
        shader.setUniform("fogColor", SceneManager.fogColor);
        shader.setUniform("fogDensity", SceneManager.fogDensity);
        shader.setUniform("fogGradient", SceneManager.fogGradient);
    }

    public void setLights(DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights){
        if(directionalLight != null){
            this.directionalLight = directionalLight;
        }
        if(pointLights != null){
            this.pointLights = pointLights;
        }
        if(spotLights != null){
            this.spotLights = spotLights;
        }
    }

    public void render(Camera camera){
        setUniform("ambientColor", Constants.AMBIENT_COLOR);
        setUniform("specularPower", Constants.SPECULAR_POWER);
        setUniform("viewPosition", camera.getPosition());

        int lightCount = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < lightCount; i++) {
            setUniform("spotLights", spotLights[i], i);
        }

        lightCount = pointLights != null ? pointLights.length : 0;
        for (int i = 0; i < lightCount; i++) {
            setUniform("pointLights", pointLights[i], i);
        }

        setUniform("directionalLight", directionalLight);
    }

    //Create uniforms
    public void createDirectionalLightUniform(String uniformName) throws Exception{
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createPointLightUniform(String uniformName) throws Exception{
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".constant");
        createUniform(uniformName + ".linear");
        createUniform(uniformName + ".exponent");
    }

    public void createPointLightArrayUniform(String uniformName, int arraySize) throws Exception{
        for (int i = 0; i < arraySize; i++) {
            createPointLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createSpotLightArrayUniform(String uniformName, int arraySize) throws Exception{
        for (int i = 0; i < arraySize; i++) {
            createSpotLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createSpotLightUniform(String uniformName) throws Exception{
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".constant");
        createUniform(uniformName + ".linear");
        createUniform(uniformName + ".exponent");
        createUniform(uniformName + ".coneDirection");
        createUniform(uniformName + ".cutOff");
        createUniform(uniformName + ".outerCutOff");
    }

    //Set uniforms
    public void setUniform(String uniformName, DirectionalLight directionalLight){
        setUniform(uniformName + ".color", directionalLight.getColor());
        setUniform(uniformName + ".direction", directionalLight.getDirection());
        setUniform(uniformName + ".intensity", directionalLight.getIntensity());
    }

    public void setUniform(String uniformName, SpotLight spotlight){
        setUniform(uniformName + ".color", spotlight.getColor());
        setUniform(uniformName + ".position", spotlight.getPosition());
        setUniform(uniformName + ".intensity", spotlight.getIntensity());
        setUniform(uniformName + ".constant", spotlight.getConstant());
        setUniform(uniformName + ".linear", spotlight.getLinear());
        setUniform(uniformName + ".exponent", spotlight.getExponent());
        setUniform(uniformName + ".coneDirection", spotlight.getConeDirection());
        setUniform(uniformName + ".cutOff", spotlight.getCutOff());
        setUniform(uniformName + ".outerCutOff", spotlight.getOuterCutOff());
    }

    public void setUniform(String uniformName, PointLight pointLight){
        setUniform(uniformName + ".color", pointLight.getColor());
        setUniform(uniformName + ".position", pointLight.getPosition());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        setUniform(uniformName + ".constant", pointLight.getConstant());
        setUniform(uniformName + ".linear", pointLight.getLinear());
        setUniform(uniformName + ".exponent", pointLight.getExponent());
    }

    public void setUniform(String uniformName, PointLight[] pointLights){
        int lightCount = pointLights != null ? pointLights.length : 0;
        for(int i = 0; i < lightCount; i++){
            setUniform(uniformName, pointLights[i], i);
        }
    }

    public void setUniform(String uniformName, SpotLight[] spotLights){
        int lightCount = spotLights != null ? spotLights.length : 0;
        for(int i = 0; i < lightCount; i++){
            setUniform(uniformName, spotLights[i], i);
        }
    }

    public void setUniform(String uniformName, PointLight pointLight, int position){
        setUniform(uniformName + "[" + position + "]", pointLight);
    }

    public void setUniform(String uniformName, SpotLight spotLight, int position){
        setUniform(uniformName + "[" + position + "]", spotLight);
    }

}
