#version 330 core

in vec2 Position;
in vec2 UV;
in vec4 Color;

out vec2 fragmentUV;
out vec4 fragmentColor;

uniform mat4 ProjMat;

void main() {
  fragmentUV = UV;
  fragmentColor = Color;
  gl_Position = ProjMat * vec4(Position, 0.0, 1.0);
}
