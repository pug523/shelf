#version 330

#define MC ${mc_version}

#if MC >= 12106
layout(std140) uniform DynamicTransforms {
  mat4 ModelViewMat;
  vec4 ColorModulator;
  vec3 ModelOffset;
  mat4 TextureMat;
  #if MC <= 12108
  float LineWidth;
  #endif
};

layout(std140) uniform Projection {
  mat4 ProjMat;
};
#else
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
#endif

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;

out vec2 texCoord0;
out vec4 vertexColor;
out float cornerRadius;
out vec2 rawSize;

void main() {
  gl_Position =
    ProjMat *
    ModelViewMat *
    vec4(Position, 1.0);
  texCoord0 = UV0;
  vertexColor = Color;

  int uBits = int(UV1.x) & 0xFFFF;
  int vBits = int(UV1.y) & 0xFFFF;
  int packedBits = (vBits << 16) | uBits;
  cornerRadius = intBitsToFloat(packedBits);
  rawSize = UV2;
}
