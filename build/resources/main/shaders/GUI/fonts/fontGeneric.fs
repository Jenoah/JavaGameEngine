#version 330

in vec2 frag_textureCoords;

out vec4 color;

uniform vec3 textColor;
uniform sampler2D fontAtlas;

void main(void){
    color = vec4(textColor, texture(fontAtlas, frag_textureCoords).a);
}