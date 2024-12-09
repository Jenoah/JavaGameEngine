#version 140

in vec2 textureCoords;

out vec4 color;

uniform sampler2D guiTexture;
uniform vec4 uiColor;
uniform int hasTexture;

void main(void){
    if(hasTexture == 1){
        color = texture(guiTexture, textureCoords) * uiColor;
    }else{
        color = uiColor;
    }
}