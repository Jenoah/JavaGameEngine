#version 140

in vec2 textureCoords;

out vec4 color;

uniform sampler2D colourTexture;
uniform float gamma = 2.2;

void main(void){
    color = texture(colourTexture, textureCoords);
    color.rgb = pow(color.rgb, vec3(1.0/gamma));
}
