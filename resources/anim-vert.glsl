#version 430

vec4 qtimes(vec4 a, vec4 b) {
  vec4 r;
  r.w = 0
    + (a.w * b.w)
    - (a.x * b.x) 
    - (a.y * b.y) 
    - (a.z * b.z);
  r.x = 
    + (a.w * b.x) 
    + (a.x * b.w) 
    - (a.y * b.z)  
    + (a.z * b.y);
  r.y = 
    + (a.w * b.y) 
    + (a.x * b.z) 
    + (a.y * b.w)  
    - (a.z * b.x);
  r.z = 
    + (a.w * b.z) 
    - (a.x * b.y) 
    + (a.y * b.x)  
    + (a.z * b.w);
  return r;
}

vec4 qvtimes(vec4 q, vec3 v) { 
  return vec4(v + 2.0*cross(cross(v, q.xyz) + q.w*v, q.xyz), 1.0);
}

vec4 qconj(vec4 q) {
  return vec4(-q.x, -q.y, -q.z, q.w);
}

vec4 qvtimes2(vec4 q, vec3 v) {
  vec4 qv = vec4(v, 0);
  vec4 cq = qconj(q);
  vec4 r = qtimes(qtimes(q, qv), cq);
  r.w = 1.0;
  return r;
}

vec4 slerp(vec4 x, vec4 y, float a) {
  float cosTheta = dot(x, y);
  vec4 z = y;
  if ( cosTheta < 0 ) {
    z = -y;
    cosTheta = -cosTheta;
  }
  if ( cosTheta > 1 - 1e-6 ) {
    return vec4(mix(x.x, z.x, a), 
                mix(x.y, z.y, a), 
                mix(x.z, z.z, a), 
                mix(x.w, z.w, a));
  } else {
    float angle = acos(cosTheta);
    float sa = sin(angle);
    float sx = sin((1 - a) * angle) / sa;
    float sz = sin(a * angle) / sa;
    return vec4(x.x*sx + z.x*sz, 
                x.y*sx + z.y*sz, 
                x.z*sx + z.z*sz, 
                x.w*sx + z.w*sz);
  }
}

layout(location = 0) in vec3 pos;
layout(location = 1) in vec2 tpos;
layout(location = 2) in vec3 normal;
layout(location = 3) in float bone;

layout(std140) uniform Q {
  vec4   q[100];        // indexed by bone and frame
};

layout(std140) uniform P {
  int    parent[100];   // indexed by bone
};

layout(std140) uniform DV {
  vec3    dv[100];      // indexed by bone, displacement from parent bone
};

layout(std140) uniform B {
  int    num_bones;
  int    num_frames;
};

uniform int    frame;
uniform float  alpha;

vec4 get_q(int frame, int bone) {
  return q[((frame%num_frames) * num_bones)+bone];
}

vec4 tr(int frame, int bone, vec3 pos) {
  return qvtimes(slerp(get_q(frame, bone), get_q(frame+1, bone), alpha), pos);
}

// passed through to fragment shader
varying vec2 frag_t;
varying vec3 frag_n;
varying vec3 frag_c;

void main(void)
{
  int vb = int(bone);
  vec4 v = tr(frame, vb, pos);
  int b = parent[vb];
  while(b >= 0) {
    v = v + tr(frame, b, dv[b]);
    if (b == 0) {
      break;
    } else {
      b = parent[b];
    }
  }
  gl_Position = gl_ModelViewProjectionMatrix * v;
  frag_t = tpos;
  frag_n = normal;
  if ( dv[0].x == 0 ) {
    frag_c = vec3(1.0, 1.0, 1.0);
  }
  else {
    frag_c = vec3(0.0, 1.0, 1.0);
  }
}
