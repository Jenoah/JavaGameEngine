#version 400 core

in vec3 textureCoords;

out vec4 outColor;

uniform samplerCube textureSampler;
uniform vec3 fogColor;

const float lowerLimit = 0;
const float upperLimit = 30;

void main() {
    vec4 finalColor = texture(textureSampler, textureCoords);

    float factor = (textureCoords.y - lowerLimit) / (upperLimit - lowerLimit);
    factor = clamp(factor, 0.0, 1.0);
    outColor = mix(vec4(fogColor, 1.0), finalColor, factor);
}
