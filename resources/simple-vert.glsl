#version 430

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 tpos;
layout(location = 2) in vec3 normal;

varying vec2 t;
varying vec3 n;

void main(void)
{
  gl_Position = gl_ModelViewProjectionMatrix * vec4(pos, 1.0);
  t = tpos;
  n = normal;
}
