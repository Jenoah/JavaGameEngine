#version 140

in vec2 position;

out vec2 textureCoords;

uniform mat4 modelMatrix;

void main(void){
    gl_Position = modelMatrix * vec4(position, 0, 1);
    textureCoords = (position + 1) / 2.0;
}