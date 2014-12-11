#version 420

uniform sampler2D tex;
varying vec2 t;

void main(void)
{
  gl_FragColor = vec4(1,1,1,1); // texture2D(tex,t);
}
