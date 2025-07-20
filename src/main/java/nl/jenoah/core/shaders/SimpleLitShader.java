package nl.jenoah.core.shaders;

import nl.jenoah.core.entity.Camera;
import nl.jenoah.core.entity.SceneManager;
import nl.jenoah.core.lighting.DirectionalLight;
import nl.jenoah.core.lighting.PointLight;
import nl.jenoah.core.lighting.SpotLight;
import nl.jenoah.core.rendering.MeshMaterialSet;
import nl.jenoah.core.utils.Constants;
import nl.jenoah.core.utils.FileHelper;
import nl.jenoah.core.utils.Transformation;
import nl.jenoah.core.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.glDepthMask;

public class SimpleLitShader extends Shader {

    private PointLight[] pointLights = new PointLight[0];
    private SpotLight[] spotLights = new SpotLight[0];
    private DirectionalLight directionalLight;
    private Matrix4f shadowSpaceMatrix = new Matrix4f();

    public SimpleLitShader() throws Exception {
        super();
        loadVertexShaderFromFile("/shaders/lit/simpleLit/vertex.vs");
        loadFragmentShaderFromFile("/shaders/lit/simpleLit/fragment.fs");
        //TODO: Make that the vertex- and fragment shaders do not get created and linked when this is called from an inheriting class
        link();
    }

    @Override
    public void createRequiredUniforms() throws Exception {
        super.createRequiredUniforms();

        createMaterialUniform("material");
        createUniform("modelMatrix");
        createUniform("viewMatrix");
        createUniform("projectionMatrix");
        createUniform("fogColor");
        createUniform("fogDensity");
        createUniform("fogGradient");

        createUniform("ambientColor");
        createUniform("specularPower");
        createUniform("viewPosition");

        createUniform("shadowMap");
        createUniform("shadowSpaceMatrix");
        createUniform("shadowDistance");
        createUniform("shadowBias");
        createUniform("shadowTransitionDistance");
        createUniform("shadowPCFCount");
        createUniform("shadowMapSize");

        createDirectionalLightUniform("directionalLight");
        createPointLightArrayUniform("pointLights", 5);
        createSpotLightArrayUniform("spotLights", 5);
    }

    @Override
    public void prepare(MeshMaterialSet meshMaterialSet, Camera camera) {
        glDepthMask(true);

        Matrix4f modelMatrix = Transformation.getModelMatrix(meshMaterialSet.getRoot());

        this.setUniform("material", meshMaterialSet.material);
        this.setUniform("modelMatrix", modelMatrix);
        this.setUniform("viewMatrix", camera.getViewMatrix());
        this.setUniform("projectionMatrix", window.getProjectionMatrix());
        this.setUniform("fogColor", SceneManager.fogColor);
        this.setUniform("fogDensity", SceneManager.fogDensity);
        this.setUniform("fogGradient", SceneManager.fogGradient);
        this.setUniform("shadowSpaceMatrix", shadowSpaceMatrix);
        this.setUniform("shadowDistance", Constants.SHADOW_DISTANCE);
        this.setUniform("shadowBias", Constants.SHADOW_BIAS);
        this.setUniform("shadowTransitionDistance", Constants.SHADOW_TRANSITION_DISTANCE);
        this.setUniform("shadowPCFCount", Constants.SHADOW_PCF_COUNT);
        this.setUniform("shadowMapSize", Constants.SHADOW_MAP_SIZE);

        /*
        if(mat.getAlbedoTexture() != null){
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getAlbedoTexture().getId());
            shader.setTexture("textureSampler", 0);
        }*/
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

        if(directionalLight != null) setUniform("directionalLight", directionalLight);
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
        Vector3f lightDir = directionalLight.getForward();
        setUniform(uniformName + ".direction", new Vector3f(lightDir));
        setUniform(uniformName + ".intensity", directionalLight.getIntensity());
    }

    public void setUniform(String uniformName, SpotLight spotlight){
        setUniform(uniformName + ".color", spotlight.getColor());
        setUniform(uniformName + ".position", spotlight.getPosition());
        setUniform(uniformName + ".intensity", spotlight.getIntensity());
        setUniform(uniformName + ".constant", spotlight.getConstant());
        setUniform(uniformName + ".linear", spotlight.getLinear());
        setUniform(uniformName + ".exponent", spotlight.getExponent());
        setUniform(uniformName + ".coneDirection", spotlight.getForward());
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

    public void setShadowSpaceMatrix(Matrix4f shadowSpaceMatrix){
        this.shadowSpaceMatrix = shadowSpaceMatrix;
    }
}
