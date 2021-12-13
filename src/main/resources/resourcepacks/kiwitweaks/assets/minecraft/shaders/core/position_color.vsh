#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out float isSpyglass;

void main() {
    isSpyglass = 0;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    if ((ModelViewMat * vec4(Position, 1.0)).z < -2050 && (ModelViewMat * vec4(Position, 1.0)).z > -2100) {
        isSpyglass = 1;
    }
    vertexColor = Color;
}
