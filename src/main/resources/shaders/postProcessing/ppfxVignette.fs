#version 330

in vec2 textureCoords;

out vec4 color;

uniform sampler2D textureSample;
uniform float falloff = .3;
uniform float amount = .4;

//Effect from https://github.com/spite/Wagner/blob/master/fragment-shaders/vignette-fs.glsl
void main(void){
    color = texture(textureSample, textureCoords);

    float dist = distance(textureCoords, vec2(0.5, 0.5));
    color.rgb *= smoothstep(0.8, falloff * 0.799, dist * (amount + falloff));
}
