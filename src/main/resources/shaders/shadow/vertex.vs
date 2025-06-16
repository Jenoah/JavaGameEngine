#version 330 core

layout (location=0) in vec3 position;
layout (location=5) in mat4 instanceModelMatrix;

uniform mat4 modelMatrix;
uniform mat4 projectionViewMatrix;

uniform bool useInstancing;

void main(void){
    mat4 finalModelMatrix = useInstancing ? instanceModelMatrix : modelMatrix;
    gl_Position = projectionViewMatrix * finalModelMatrix * vec4(position, 1.0);
}