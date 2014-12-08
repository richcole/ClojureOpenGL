#version 420

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 tpos;

varying vec2 t;

void main(void)
{
  gl_Position = gl_ModelViewProjectionMatrix * vec4(pos, 1.0);
  t = tpos;
}
