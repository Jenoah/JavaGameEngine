#version 140

in vec2 textureCoords;

out vec4 color;

uniform sampler2D guiTexture;
uniform vec4 uiColor;

void main(void){
    color = texture(guiTexture, textureCoords) * uiColor;
}