package game;

import game.math.Matrix;
import game.math.Vector;


public class GrayCode {
  
  static private double[] G = { -1, -1, -1, 1, 1, 1, 1, -1 };
  
  static {
    
  }
  
  int x0;
  int x1;
  int x2;
  Matrix m;
  Vector n;

  public GrayCode(int x0, int x1, int x2, double sign, int dirn, int reflx, int refly) {
    this.x0 = x0;
    this.x1 = x1;
    this.x2 = x2;

    m = new Matrix();
    m.set(0, x0, get(0, dirn)*reflx);
    m.set(0, x1, get(1, dirn)*refly);
    m.set(0, x2, sign);
    m.set(1, x0, get(2, dirn)*reflx);
    m.set(1, x1, get(3, dirn)*refly);
    m.set(1, x2, sign);
    m.set(2, x0, get(4, dirn)*reflx);
    m.set(2, x1, get(5, dirn)*refly);
    m.set(2, x2, sign);
    m.set(3, x0, get(6, dirn)*reflx);
    m.set(3, x1, get(7, dirn)*refly);
    m.set(3, x2, sign);
    
    n = new Vector();
    n.set(x0, 0d);
    n.set(x1, 0d);
    n.set(x2, sign);
    n.set(3,  1d);
  }

  private double get(int i, int dirn) {
    return G[(i + dirn*2)%G.length];
  }
  
  public Matrix getCode() {
    return m;
  }

  public Vector getNormal() {
    return n;
  }
}
