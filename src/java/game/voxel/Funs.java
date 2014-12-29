package game.voxel;

import game.math.Vector;

public class Funs {

  static public double smoothStep(double a, double b, double x) {
    if ( x < a ) {
      return 0;
    }
    if ( x > b ) {
      return 1;
    }
    x = (x - a)/(b - a);
    return x*x * (3 - 2*x);
  }
  
  static public double clamp(double a, double b, double x) {
    if ( x < a ) {
      return 0;
    }
    if ( x > b ) {
      return 1;
    }
    return x;
  }
  
  static public boolean isPos(double x) {
    return x > 0.5;
  }

  static public Vector midPoint(double d1, double d2, Vector p1, Vector p2) {
    return p1.plus(p2.minus(p1).times((d1 - 0.5) / (d1 - d2)));
  }

  public static boolean inSquare(Vector c, double d, Vector p) {
    Vector r = p.minus(c);
    return (Math.abs(r.x()) <= d) && (Math.abs(r.y()) <= d) && (Math.abs(r.z()) <= d);
  }

}
