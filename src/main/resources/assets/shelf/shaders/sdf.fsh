#version 330

in vec2 fragmentUV;
in vec4 fragmentColor;

out vec4 fragColor;

layout(std140) uniform SdfParams {
  vec4 Params;
};

float sdfRoundedRect(vec2 p, vec2 halfSize, float r) {
  float safeR = min(r, min(halfSize.x, halfSize.y));

  vec2 q = abs(p) - halfSize + vec2(safeR);
  return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - safeR;
}

void main() {
  vec2 Size = Params.xy;
  float CornerRadius = Params.z;

  vec2 halfSize = Size * 0.5;
  vec2 p = (fragmentUV - 0.5) * Size;

  float d = sdfRoundedRect(p, halfSize, CornerRadius);

  float edgeThickness = fwidth(d);
  // float edgeThickness = 1.0;
  float alpha = smoothstep(edgeThickness, 0.0, d);

  // if (alpha <= 0.0) discard;

  fragColor = vec4(fragmentColor.rgb, fragmentColor.a * alpha);
}
