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

layout(std140) uniform SdfParamsUbo {
  vec4 SdfParams;
};
#else
uniform vec4 ColorModulator;
uniform vec4 SdfParams;
#endif

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float sdfRoundedRect(vec2 p, vec2 halfSize, float r) {
  float safeR = min(r, min(halfSize.x, halfSize.y));

  vec2 q = abs(p) - halfSize + vec2(safeR);
  return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - safeR;
}

void main() {
  vec2 rawSize = SdfParams.xy;
  float CornerRadius = SdfParams.z;

  // 0.5 ~ 1.5 is good
  float edgeThickness = 0.75;

  vec2 playableSize = rawSize - vec2(edgeThickness * 2.0);
  vec2 halfSize = playableSize * 0.5;

  vec2 p = (texCoord0 - 0.5) * rawSize;

  float d = sdfRoundedRect(p, halfSize, CornerRadius);

  float alpha = smoothstep(edgeThickness, 0.0, d);

  if (alpha <= 0.0) discard;

  vec4 color = vec4(vertexColor.rgb, vertexColor.a * alpha);
  fragColor = color * ColorModulator;
}
