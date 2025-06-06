#version 140

in vec2 position;

out vec2 blurTextureCoords[11];
out float targetSize;

uniform int targetHeight;

void main(void){
    gl_Position = vec4(position, 0.0, 1.0);

    vec2 textureCoords = position * 0.5 + 0.5;
    float pixelSize = 1.0 / targetHeight;
    targetSize = targetHeight;

    for(int i = -5; i <= 5; i++){
        blurTextureCoords[i + 5] = textureCoords + vec2(0.0, pixelSize * i);
    }
}