#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoords;
layout (location=2) in vec3 normal;
layout (location=3) in vec3 tangents;
layout (location=4) in vec3 bitangents;

out vec3 fragPosition;
out vec2 UV;
out mat3 TBN;
out float fogFactor;

//TODO: CALC TANGENT LIGHT POSITION, VIEWPOSITION AND FRAG POSITION IN VERTEX SHADER INSTEAD OF RECALCULATING FOR FRAGMENT AND JUST LET IT LERP BETWEEN AUTOMATICALLY

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform float fogDensity;
uniform float fogGradient;

void main(){
    vec4 worldPosition = modelMatrix * vec4(position, 1.0);
    vec4 cameraObjectPosition = viewMatrix * worldPosition;

    vec3 T = normalize(vec3(modelMatrix * vec4(tangents, 0.0)));
    vec3 B = normalize(vec3(modelMatrix * vec4(bitangents, 0.0)));
    vec3 N = normalize(vec3(modelMatrix * vec4(normal, 0.0)));
    TBN = mat3(T,B,N);

    gl_Position = projectionMatrix * cameraObjectPosition;

    fragPosition = worldPosition.xyz;
    UV = textureCoords;

    float cameraDistance = length(cameraObjectPosition.xyz);
    fogFactor = clamp(exp(-pow((cameraDistance*fogDensity), fogGradient)), 0.0, 1.0);
}