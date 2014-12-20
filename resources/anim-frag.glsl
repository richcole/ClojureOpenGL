#version 420

uniform sampler2D tex;
varying vec2 frag_t;
varying vec3 frag_n;
varying vec3 frag_c;

void main(void)
{
  // gl_FragColor = vec4(frag_c, 1.0); 
  gl_FragColor = texture2D(tex, frag_t) * (0.8 + (0.2 * abs(frag_n[1]))); 
  gl_FragColor[3] = 1.0;
}
