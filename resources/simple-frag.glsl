#version 420

uniform sampler2D tex;
varying vec2 t;
varying vec3 n;

void main(void)
{
  gl_FragColor = texture2D(tex, t) * (0.8 + (0.2 * abs(n[1]))); 
  gl_FragColor[3] = 1.0;
}
