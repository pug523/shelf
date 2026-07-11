#version 330

in vec3 Position;
in vec2 UV0;
in vec4 Color;

//#if MC >= 12106
layout(std140) uniform DynamicTransforms {
  mat4 ModelViewMat;
  vec4 ColorModulator;
  vec3 ModelOffset;
  mat4 TextureMat;
};

layout(std140) uniform Projection {
  mat4 ProjMat;
};
//#else
//$$ uniform mat4 ModelViewMat;
//$$ uniform mat4 ProjMat;
//#endif

out vec2 fragmentUV;
out vec4 fragmentColor;

void main() {
  fragmentUV = UV0;
  fragmentColor = Color;

  gl_Position =
    ProjMat *
    ModelViewMat *
    vec4(Position, 1.0);
}
