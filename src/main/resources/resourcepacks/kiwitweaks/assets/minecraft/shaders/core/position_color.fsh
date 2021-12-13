#version 150


in vec4 vertexColor;
in float isSpyglass;

uniform vec4 ColorModulator;
uniform vec2 ScreenSize;
uniform mat4 ModelViewMat;

out vec4 fragColor;

void main() {
    vec4 color = vertexColor;
    if (color.a == 0.0) discard;
    if (isSpyglass > 0.5 && color == vec4(0,0,0,1)) color.a = 0.55;
    fragColor = color * ColorModulator;
}
