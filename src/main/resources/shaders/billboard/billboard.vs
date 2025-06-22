#version 400 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoords;

out vec2 texCoords;
out float fogFactor;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 objectPosition;
uniform vec3 modelScale;
uniform float fogDensity;
uniform float fogGradient;

void main(){
    vec3 cameraRight = vec3(viewMatrix[0][0], viewMatrix[1][0], viewMatrix[2][0]); // Right vector
    vec3 cameraUp = vec3(viewMatrix[0][1], viewMatrix[1][1], viewMatrix[2][1]); // Up vector

    vec3 scaledPosition = position * modelScale;
    vec3 vertexPosition = objectPosition + cameraRight * scaledPosition.x + cameraUp * scaledPosition.y;

    vec4 cameraObjectPosition = viewMatrix * vec4(vertexPosition, 1.0);
    gl_Position = projectionMatrix * cameraObjectPosition;
    texCoords = textureCoords;

    float cameraDistance = length(cameraObjectPosition.xyz);
    fogFactor = clamp(exp(-pow((cameraDistance*fogDensity), fogGradient)), 0.0, 1.0);
}