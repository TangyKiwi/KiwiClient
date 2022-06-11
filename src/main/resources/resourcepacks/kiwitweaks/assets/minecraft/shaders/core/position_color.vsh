#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    if ((ModelViewMat * vec4(Position, 1.0)).z < -2000) vertexColor = vec4(0.0, 0.0, 0.0, 0.55);
    else vertexColor = Color;

}
