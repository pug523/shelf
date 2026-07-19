#version 150

uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;
in float cornerRadius;
in vec2 rawSize;

out vec4 fragColor;

float sdfRoundedRect(vec2 p, vec2 halfSize, float r) {
  float safeR = min(r, min(halfSize.x, halfSize.y));

  vec2 q = abs(p) - halfSize + vec2(safeR);
  return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - safeR;
}

void main() {
  // 0.5 ~ 1.5 is good
  float edgeThickness = 0.75;

  vec2 playableSize = rawSize - vec2(edgeThickness * 2.0);
  vec2 halfSize = playableSize * 0.5;

  vec2 p = (texCoord0 - 0.5) * rawSize;

  float d = sdfRoundedRect(p, halfSize, cornerRadius);

  float alpha = smoothstep(edgeThickness, 0.0, d);

  if (alpha <= 0.0) discard;

  vec4 color = vec4(vertexColor.rgb, vertexColor.a * alpha);
  fragColor = color * ColorModulator;
}
