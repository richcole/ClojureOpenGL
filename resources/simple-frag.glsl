#version 420

uniform sampler2D tex;
varying vec2 t;
varying vec3 n;

void main(void)
{
  vec4 tc   = texture2D(tex, t);
  gl_FragColor = tc * (0.8 + (0.2 * abs(n[1]))); 
  if (abs(t.x - 0.5) > 0.49 || abs(t.y - 0.5) > 0.49) {
    gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
  }
  gl_FragColor.w = tc.w;
}
