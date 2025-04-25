#version 400 core
const int MAXIMUM_POINT_LIGHTS = 5;
const int MAXIMUM_SPOT_LIGHTS = 5;
const float PI = 3.14159265359;

in vec2 UV;
in vec3 fragPosition;
in float fogFactor;
in mat3 TBN;

out vec4 color;

struct Material {
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct PointLight{
    vec3 color;
    vec3 position;
    float intensity;
    float constant;
    float linear;
    float exponent;
};

//Refactor to use a pointlight and just add the last three variables from the spotlight struct
struct SpotLight{
    vec3 color;
    vec3 position;
    float intensity;
    float constant;
    float linear;
    float exponent;
    vec3 coneDirection;
    float cutOff;
    float outerCutOff;
};

uniform Material material;
uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform int hasAlbedoMap = 0;
uniform int hasNormalMap = 0;
uniform float metallic = 0.1;
uniform float roughness = 0.8;

uniform vec3 ambientColor;
uniform vec3 viewPosition;
uniform vec3 fogColor;

uniform DirectionalLight directionalLight;
uniform PointLight pointLights[MAXIMUM_POINT_LIGHTS];
uniform SpotLight spotLights[MAXIMUM_SPOT_LIGHTS];

vec3 fragNormal;
vec3 viewDirection;

vec3 ambient;
vec4 albedo;

uniform float specularPower = 1.0;

//HELPER FUNCTIONS
vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}
float DistributionGGX(vec3 N, vec3 H, float roughness)
{
    float a      = roughness*roughness;
    float a2     = a*a;
    float NdotH  = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float num   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return num / denom;
}
float GeometrySchlickGGX(float NdotV, float roughness)
{
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float num   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return num / denom;
}
float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness)
{
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2  = GeometrySchlickGGX(NdotV, roughness);
    float ggx1  = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}

vec3 calculatePointLight(PointLight light){
    //TODO: This doesn't work. Just make a generic function for lighting and use https://learnopengl.com/PBR/Lighting
    //TODO: Or better look at this and fix it (line 618) https://github.com/emeiri/ogldev/blob/master/Common/Shaders/lighting_new.fs

    vec3 lightDirection = light.position - fragPosition;

    //Distance
    float distance = length(lightDirection);
    lightDirection = normalize(lightDirection);
    float attenuation = light.intensity / (distance * distance);

    vec3 halfVector = normalize(viewDirection + lightDirection);

    float dotNormalHalf = max(dot(fragNormal, halfVector), 0.0);
    float dotViewHalf = max(dot(viewDirection, halfVector), 0.0);
    float dotNormalL = max(dot(fragNormal, lightDirection), 0.0);
    float dotNormalView = max(dot(fragNormal, viewDirection), 0.0);

    vec3 F0 = vec3(0.04);
    F0      = mix(F0, albedo.rgb, metallic);
    vec3 F  = fresnelSchlick(dotViewHalf, F0);
    vec3 kS = F;
    vec3 kD = 1.0 - kS;

    vec3 specBRDF_nom = DistributionGGX(fragNormal, halfVector, roughness) * F * GeometrySmith(fragNormal, viewDirection, lightDirection, roughness);
    float specBRDF_denom = 4.0 * dotNormalView * dotNormalL + 0.0001;
    vec3 specBRDF = specBRDF_nom / specBRDF_denom;

    vec3 fLambert = vec3(0);

    //IF IS METAL, fLambert IS THE METAL MAP COLOR?

    vec3 diffuseBRDF = kD * fLambert / PI;
    return (diffuseBRDF + specBRDF) * attenuation * dotNormalL;

}

vec3 calculateSpotLight(SpotLight light){
    vec3 lightDirection = normalize(light.position - fragPosition);

    float theta = dot(lightDirection, normalize(-light.coneDirection));
    float epsilon = light.cutOff - light.outerCutOff;
    float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0, 1.0);

    vec3 outColor = vec3(0);

    if(theta > light.outerCutOff){
        //Diffuse
        float diffuseInfluence = max(dot(fragNormal, lightDirection), 0.0);

        //Specular
        vec3 reflectedDirection = reflect(-lightDirection, fragNormal);
        float specularInfuence = pow(max(dot(viewDirection, reflectedDirection), 0.0), 32);

        //Distance
        float distance = length(light.position - fragPosition);
        float attenuation = 1.0 / (light.constant + light.linear * distance + light.exponent * (distance * distance));

        //Calculation
        vec3 ambientOutput = ambient * albedo.rgb * attenuation;
        vec3 diffuseOutput = light.color * diffuseInfluence * albedo.rgb * attenuation * light.intensity;
        vec3 specularOutput = light.color * specularInfuence * specularPower * attenuation * light.intensity;

        outColor = (ambientOutput + diffuseOutput + specularOutput);

        float fade = smoothstep(light.outerCutOff, light.cutOff, theta);
        outColor *= fade;
    }

    return outColor;
}

vec3 calculateDirectionalLight(DirectionalLight light){
    vec3 lightDirection = normalize(light.direction);

    //Diffuse
    float diffuseInfluence = max(dot(fragNormal, lightDirection), 0.0);

    //Specular
    vec3 reflectedDirection = reflect(-lightDirection, fragNormal);
    float specularInfuence = pow(max(dot(viewDirection, reflectedDirection), 0.0), 32);
    specularInfuence = max(specularInfuence, 0.0);

    //Calculation
    vec3 ambientOutput = ambient * albedo.rgb;
    vec3 diffuseOutput = light.color * diffuseInfluence * albedo.rgb;
    vec3 specularOutput = light.color * specularInfuence * specularPower;
    specularOutput = vec3(0);

    return (ambientOutput + diffuseOutput + specularOutput);
}

vec4 getAlbedo(){
    vec4 albedo = vec4(0);
    if(hasAlbedoMap == 1){
        vec4 albedoTexture = texture(albedoMap, UV) * material.diffuse;
        albedo = albedoTexture;
    }else{
        albedo = material.diffuse;
    }

    return albedo;
}

vec3 getNormal(){
    vec3 normal  = vec3(0);
    if(hasNormalMap == 1){
        normal = texture(normalMap, UV).rgb;
        normal = normalize(normal * 2.0 - 1.0);
        //fragNormal = mix(fragNormal, TBN[2], 0.5); //FAKE STRENGTH?
    }else{
        normal = normalize(TBN[2]);
    }

    return normal;
}

void main() {
    //Declare textures
    albedo = getAlbedo();
    fragNormal = getNormal();

    ambient = ambientColor * 0.3;

    //fragNormal = normalize(normal);
    viewDirection = normalize(viewPosition - fragPosition);

    //Directional Light
    color.rgb = calculateDirectionalLight(directionalLight);

    for(int i = 0; i < MAXIMUM_POINT_LIGHTS; i++){
        if(pointLights[i].intensity > 0){
            color.rgb += calculatePointLight(pointLights[i]);
        }
    }

    for(int i = 0; i < MAXIMUM_SPOT_LIGHTS; i++){
        if(spotLights[i].intensity > 0){
            color.rgb += calculateSpotLight(spotLights[i]);
        }
    }

    //color = color / (color + vec4(1.0)); //HDR?
    color.a = albedo.a;
    color.rgb = mix(vec4(fogColor, 1.0), color, fogFactor).rgb;
}
