#version 400 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoords;
layout (location=2) in vec3 normal;

out vec3 vertexNormal;
out vec3 vertexColor;
out vec3 fragPosition;
out vec2 texCoords;
out float fogFactor;

uniform vec2 terrainHeight;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform float fogDensity;

const float fogGradient = 1.5;
const vec3 bottomColor = vec3(1, .75, .3);
const vec3 middleColor = vec3(0, .75, .3);
const vec3 topColor = vec3(.8, .8, .8);
const float middleColorInfluence = 0.8;

void main(){
    vec4 worldPosition = modelMatrix * vec4(position, 1.0);
    vec4 cameraObjectPosition = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * cameraObjectPosition;

    vertexNormal = normal;

    float normalizedY = (worldPosition.y - terrainHeight.x) / (terrainHeight.y - terrainHeight.x); // Assumes worldPos.y ranges from -1 to 1

    float lowerBoundMiddleColor = 0.5 - middleColorInfluence * 0.5;  // Lower bound for color B blending
    float upperBoundMiddleColor = 0.5 + middleColorInfluence * 0.5;  // Upper bound for color B blending


    float mixAB = smoothstep(0.0, lowerBoundMiddleColor, normalizedY); // Smooth blend between A and B
    float mixBC = smoothstep(upperBoundMiddleColor, 1.0, normalizedY); // Smooth blend between B and C


    vertexColor = mix(bottomColor, middleColor, mixAB); // Blend between A and B
    vertexColor = mix(vertexColor, topColor, mixBC); // Then blend between previous result and C


    fragPosition = worldPosition.xyz;
    texCoords = textureCoords;

    float cameraDistance = length(cameraObjectPosition.xyz);
    fogFactor = clamp(exp(-pow((cameraDistance*fogDensity), fogGradient)), 0.0, 1.0);
}