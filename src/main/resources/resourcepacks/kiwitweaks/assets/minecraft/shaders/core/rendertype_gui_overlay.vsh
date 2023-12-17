#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;

void main() {

    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    float depth = (ModelViewMat * vec4(Position, 1.0)).z;
    
    if (-11100 < depth && depth < -11075) {
        vertexColor = vec4(0.0, 0.0, 0.0, 0.55);
    } else {
        vertexColor = Color;
    }

}
