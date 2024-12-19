#version 400 core
in vec2 texCoords;
in float fogFactor;

out vec4 outColor;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

uniform sampler2D textureSampler;
uniform vec3 fogColor;
uniform Material material;

void main() {
    if(material.hasTexture == 1){
        outColor = texture(textureSampler, texCoords) * material.ambient;
    }else{
        outColor = material.ambient;
    }

    outColor.rgb = mix(vec4(fogColor, 1.0), outColor, fogFactor).rgb; }
