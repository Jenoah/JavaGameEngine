#version 150

in vec2 textureCoords;

out vec4 color;

uniform sampler2D colorTexture;
uniform float threshold;

void main(void){
    vec4 sceneColor = texture(colorTexture, textureCoords);
    float brightness = (sceneColor.r * 0.2126) + (sceneColor.g * 0.7152) + (sceneColor.b * 0.0722);
    if(brightness >= threshold){
        color = sceneColor;
    }else{
        color = vec4(0, 0, 0, 1);
    }
}