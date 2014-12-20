#version 420

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 tpos;
layout(location = 2) in vec3 normal;
layout(location = 3) in int  bone;

uniform Q {
  mat4   q[1];        // transformations
};

uniform P {
  int    parent[1];   // parent for each bone
};

uniform QI {
  int    q_index[1];  // q index for each bone
};

// passed through to fragment shader
varying vec2 frag_t;
varying vec3 frag_n;
varying vec3 frag_c;

void main(void)
{
  mat4 tr = q[q_index[bone]];
  int b = parent[bone];
  while(b > 0) {
     tr = tr * q[q_index[b]];
     b = parent[b] - 1;
  }
  gl_Position = gl_ModelViewProjectionMatrix * tr * vec4(pos, 1.0);
  frag_t = tpos;
  frag_n = normal;
}
