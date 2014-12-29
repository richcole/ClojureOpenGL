package game.math;

import java.nio.FloatBuffer;


public class Matrix {

  double v[] = new double[16];
  
  public static final Matrix IDENTITY = Matrix.id();
  public static final Matrix BASIS    = Matrix.rows(Vector.U1, Vector.U2, Vector.U3, Vector.Z);
  public static final Matrix ONES     = Matrix.ones();
  public static final Matrix ZERO     = IDENTITY.minus(IDENTITY);
  
  public Matrix times(Matrix m) {
    Matrix r = new Matrix();
    for(int i=0;i<4;++i) {
      for(int j=0;j<4;j++) {
        int index = i*4 + j;
        for(int k=0;k<4;++k) {
          r.v[index] += v[i*4+k] * m.v[k*4+j];
        }
      }
    }
    return r;
  }
  
  public Matrix(double ... v) {
    if ( v.length == 0 ) {
      this.v =  new double[16];
    } else {
      this.v = v;
    }
  }
  
  Matrix(float q1) {
    
  }

  public Matrix(Matrix o) {
    v = new double[16];
    for(int i=0;i<16;++i) {
      v[i] = o.v[i];
    }
  }

  public static Matrix ones() {
    Matrix r = new Matrix();
    for(int i=0;i<16;++i) {
      r.v[i] = 1;
    }
    return r;
  }

  public Vector times(Vector o) {
    Vector r = new Vector();
    for(int i=0;i<4;++i) {
      for(int k=0;k<4;++k) {
        r.v[i] += v[i*4+k] * o.v[k];
      }
    }
    return r;
  }
  
  public static Matrix skew(Vector v) {
    Matrix r = new Matrix();
    r.v[1] = -v.v[2];
    r.v[2] = v.v[1];
    r.v[4] = v.v[2];
    r.v[6] = -v.v[0];
    r.v[8] = -v.v[1];
    r.v[9] = v.v[0];
    r.v[15] = 1.0;
    return r;
  }
  
  public static Matrix square(Vector v) {
    Matrix r = new Matrix();
    for(int i=0;i<4;++i) {
      for(int j=0;j<4;j++) {
        r.v[i*4+j] += v.v[i] * v.v[j];
      }
    }
    return r;
  }
  
  public static Matrix rows(Vector v1, Vector v2, Vector v3, Vector v4) {
    Matrix r = new Matrix();
    Vector v[] = new Vector[4];
    v[0] = v1;
    v[1] = v2;
    v[2] = v3;
    v[3] = v4;
    for(int i=0;i<4;++i) {
      for(int j=0;j<4;++j) {
        r.v[i*4+j] = v[i].v[j];
      }
    }
    return r;
  }
  
  public static Matrix columns(Vector v1, Vector v2, Vector v3, Vector v4) {
    return Matrix.rows(v1, v2, v3, v4).transpose();
  }
  
  public Matrix transpose() {
    Matrix r = new Matrix();
    for(int i=0;i<4;++i) {
      for(int j=0;j<4;++j) {
        r.v[i*4+j] = v[j*4+i];
      }
    }
    return r;
  }

  public static Matrix id() {
    Matrix r = new Matrix();
    for(int i=0;i<4; ++i) {
      r.v[i*4+i] = 1;
    }
    return r;
  }
  
  public Matrix minus(Matrix o) {
    Matrix r = new Matrix();
    for(int i=0;i<16;++i) {
      r.v[i] = v[i] - o.v[i];
    }
    return r;
  }
  
  public Matrix plus(Matrix o) {
    Matrix r = new Matrix();
    for(int i=0;i<16;++i) {
      r.v[i] = v[i] + o.v[i];
    }
    return r;
  }
  
  public Matrix times(double d) {
    Matrix r = new Matrix();
    for(int i=0;i<16;++i) {
      r.v[i] = v[i] * d;
    }
    return r;
  }

  public static Matrix rot(double theta, Vector x) {
    x = x.normalize();
    Matrix square = Matrix.square(x);
    Matrix r = square.plus(Matrix.IDENTITY.minus(square).times(Math.cos(theta)).plus(Matrix.skew(x).times(Math.sin(theta))));
    for(int i=0; i<4; ++i) {
      r.v[i*4+3] = i == 3 ? 1 : 0;
      r.v[3*4+i] = i == 3 ? 1 : 0;
    }
    return r;
  }
  
  public static Matrix translate(Vector x) {
    Matrix tr = new Matrix(Matrix.IDENTITY);
    for(int i=0;i<3;++i) {
      tr.v[i*4 + 3] = x.v[i];
    }
    return tr;
  }
  
  public static Matrix scale(double x) {
    Matrix tr = new Matrix(Matrix.ZERO);
    for(int i=0;i<4;++i) {
      tr.v[i*4] = x;
    }
    tr.v[15] = 1.0;
    return tr;

  }
  
  public static Matrix scale(Vector x) {
    Matrix tr = new Matrix(Matrix.ZERO);
    for(int i=0;i<4;++i) {
      tr.v[i*4] = x.v[i];
    }
    return tr;
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append("[ ");
    for(int i=0;i<16;++i) {
      b.append(String.format("%2.2f", v[i]));
      if ( i != 15 ) {
        if ( i % 4 == 3 ) {
          b.append("; ");
        }
        else {
          b.append(", ");
        }
      }
    }
    b.append(" ]");
    return b.toString();
  }
  
  public boolean withinDelta(Matrix o, double delta) {
    for(int i=0;i<4;++i) {
      for(int j=0;j<3;++j) {
        if ( Math.abs(v[i*4+j]/v[i*4+3] - o.v[i*4+j]/o.v[i*4+3]) > delta ) {
          return false;
        }
      }
    }
    return true;
  }

  public Vector row(int i) {
    Vector r = new Vector();
    for(int j=0;j<4;++j) {
      r.v[j] = v[i*4+j];
    }
    return r;
  }

  public Vector col(int j) {
    Vector r = new Vector();
    for(int i=0;i<4;++i) {
      r.v[i] = v[i*4+j];
    }
    return r;
  }

  public double get(int i, int j) {
    return v[i*4+j];
  }

  public double get(int i) {
    return v[i];
  }

  public void set(int i, int j, double value) {
    v[i*4+j] = value;
  }

  public void writeToBuffer(FloatBuffer tr) {
    tr.rewind();
    for(int i=0;i<v.length;++i) {
      tr.put((float)v[i]);
    }
    tr.flip();
  }

}
