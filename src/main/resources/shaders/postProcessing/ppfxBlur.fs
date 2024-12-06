#version 150

in vec2 blurTextureCoords[11];
in vec2 textureCoords;
in float targetSize;

out vec4 color;

uniform sampler2D colourTexture;

void main(void){
    color = vec4(0.0);
    color += texture(colourTexture, blurTextureCoords[0]) * 0.0093;
    color += texture(colourTexture, blurTextureCoords[1]) * 0.028002;
    color += texture(colourTexture, blurTextureCoords[2]) * 0.065984;
    color += texture(colourTexture, blurTextureCoords[3]) * 0.121703;
    color += texture(colourTexture, blurTextureCoords[4]) * 0.175713;
    color += texture(colourTexture, blurTextureCoords[5]) * 0.198596;
    color += texture(colourTexture, blurTextureCoords[6]) * 0.175713;
    color += texture(colourTexture, blurTextureCoords[7]) * 0.121703;
    color += texture(colourTexture, blurTextureCoords[8]) * 0.065984;
    color += texture(colourTexture, blurTextureCoords[9]) * 0.028002;
    color += texture(colourTexture, blurTextureCoords[10]) * 0.0093;

}