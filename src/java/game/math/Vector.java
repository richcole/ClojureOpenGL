package game.math;


import java.util.Arrays;

import org.lwjgl.opengl.GL11;

public class Vector {

  double v[] = new double[4];
  
  public static final Vector LEFT   = new Vector(-1,  0,  0, 1);
  public static final Vector UP     = new Vector(0,  0,  1, 1);
  public static final Vector NORMAL = new Vector(0,  1,  0, 1);
  public static final Vector U1     = new Vector(1,  0,  0, 1);
  public static final Vector U2     = new Vector(0,  1,  0, 1);
  public static final Vector U3     = new Vector(0,  0,  1, 1);
  public static final Vector M1     = new Vector(-1,  0,  0, 1);
  public static final Vector M2     = new Vector(0,  -1,  0, 1);
  public static final Vector M3     = new Vector(0,  0,  -1, 1);
  public static final Vector Z      = new Vector(0,  0,  0, 1);
  public static final Vector ONES   = new Vector(1,  1,  1, 1);
  
  static {
    if ( ! LEFT.cross(UP).equals(NORMAL) ) {
      throw new RuntimeException("Not right handed " + LEFT.cross(UP) + " " + NORMAL);
    }
  }
  
  public Vector(double x1, double x2, double x3, double x4) {
    v[0] = x1;
    v[1] = x2;
    v[2] = x3;
    v[3] = x4;
  }
  
  public Vector(float x1, float x2, float x3) {
    v[0] = x1;
    v[1] = x2;
    v[2] = x3;
    v[3] = 1;
  }

  public Vector() {
  }

  public Vector(Vector o) {
    for(int i=0;i<4;++i) {
      v[i] = o.v[i];
    }
  }

  public Vector(double[] v) {
    this.v = v;
  }

  public Vector times(double s) {
    return new Vector(v[0] * s, v[1] * s, v[2] * s, v[3]);
  }
  
  public Vector project() {
    return new Vector(v[0]/v[3], v[1]/v[3], v[2]/v[3], 1.0);
  }
  
  public Vector normalize() {
    return new Vector(v[0], v[1], v[2], unscaledLength());
  }

  public double unscaledLength() {
    return Math.sqrt(unscaledLengthSquared());
  }

  public double unscaledLengthSquared() {
    return v[0]*v[0] + v[1]*v[1] + v[2]*v[2];
  }
  
  public double length() {
    return unscaledLength() / v[3];
  }
  
  public Vector cross(Vector o) {
    return Matrix.skew(this).times(o);
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append("[ ");
    for(int i=0;i<4;++i) {
      b.append(String.format("%2.2f", v[i]));
      if ( i != 3 ) {
        b.append(", ");
      }
    }
    b.append(" ]");
    return b.toString();
  }

  public boolean withinDelta(Vector o, double delta) {
    for(int i=0;i<3;++i) {
      if ( Math.abs(v[i]/v[4] - o.v[i]/o.v[4]) > delta ) {
        return false;
      }
    }
    return true;
  }

  public Vector minus() {
    return times(-1);
  }
  
  public void glTranslate() {
    GL11.glTranslated(v[0]/v[3], v[1]/v[3], v[2]/v[3]);
  }

  public void glRotate(double theta) {
    GL11.glRotated(theta * 180 / Math.PI , v[0]/v[3], v[1]/v[3], v[2]/v[3]);
  }

  public Vector times(Matrix m) {
    Vector r = new Vector();
    for(int j=0;j<4;++j) {
      for(int k=0;k<4;++k) {
        r.v[j] += v[k] * m.v[k*4+j];
      }
    }
    return r;
  }

  public Vector scaleTo(double s) {
    if (s < 1e-6) {
      return new Vector(0, 0, 0, 1);
    } else {
      Vector r = new Vector(this);
      r.v[3] = unscaledLength() / s;
      return r;
    }
  }

  public Vector plus(Vector o) {
    Vector r = new Vector();
    for(int i=0;i<3;++i) {
      r.v[i] = v[i]/v[3] + o.v[i]/o.v[3];
    }
    r.v[3] = 1.0;
    return r;
  }

  public double x() {
    return v[0] / v[3];
  }
    
  public double y() {
    return v[1] / v[3];
  }

  public double z() {
    return v[2] / v[3];
  }
  
  public double w() {
    return v[3];
  }

  public Vector minus(Vector o) {
    Vector r = new Vector();
    for(int i=0;i<3;++i) {
      r.v[i] = v[i]/v[3] - o.v[i]/o.v[3];
    }
    r.v[3] = 1.0;
    return r;
  }

  public double lengthSquared() {
    return unscaledLengthSquared() / (v[3] * v[3]);
  }

  public double get(int i) {
    return v[i];
  }

  public void set(int i, double value) {
    v[i] = value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(v);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Vector other = (Vector) obj;
    if (!Arrays.equals(v, other.v))
      return false;
    return true;
  }

  public double dot(Vector o) {
    return (v[0]*o.v[0] + v[1]*o.v[1] + v[2]*o.v[2]) /  (v[3]*o.v[3]);
  }
  
  public double theta(Vector u1, Vector u2) {
    double x1 = dot(u1) / (length()*u1.length());
    double x2 = dot(u2) / (length()*u2.length());
    return Math.atan2(x2, x1);
  }

  public double[] toDoubleArray() {
    return v;
  }

  public Vector elementTimes(Vector o) {
    return new Vector(v[0]*o.x(), v[1]*o.y(), v[2]*o.z(), v[3]);
  }

  public Vector divide(Vector o) {
    return new Vector(v[0]/o.v[0], v[1]/o.v[1], v[2]/o.v[2], v[3]/o.v[3]);
  }

  public Vector floor() {
    return new Vector(Math.floor(x()), Math.floor(y()), Math.floor(z()), 1);
  }

  public Vector modulo(double grain) {
    return new Vector(Math.floor(x() / grain)*grain, Math.floor(y() / grain)*grain, Math.floor(z() / grain)*grain, 1.0);
  }
  
}
