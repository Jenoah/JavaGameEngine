#version 330 core

out vec4 out_color;

void main(void){
    gl_FragDepth = gl_FragCoord.z;
}