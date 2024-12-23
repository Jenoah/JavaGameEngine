#version 140

in vec2 textureCoords;

out vec4 color;

uniform sampler2D colourTexture;

const float contrast = .02;

void main(void){
    color = texture(colourTexture, textureCoords);
    color.rgb = (color.rgb - 0.5) * (1.0 + contrast) + 0.5;
}
