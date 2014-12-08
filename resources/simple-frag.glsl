#version 420

uniform sampler2D tex;
varying vec2 t;

void main(void)
{
  gl_FragColor = texture2D(tex,t);
}
