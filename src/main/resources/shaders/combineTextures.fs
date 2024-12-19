#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D primaryTexture;
uniform sampler2D secondaryTexture;
uniform float secondaryIntensity;

void main(void){
    vec4 primaryColor = texture(primaryTexture, textureCoords);
    vec4 secondaryColor = texture(secondaryTexture, textureCoords) * secondaryIntensity;

    color = primaryColor + (secondaryColor * secondaryIntensity);
}