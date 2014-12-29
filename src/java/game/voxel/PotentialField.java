package game.voxel;

import game.math.Vector;

public class PotentialField implements DensityFunction {
  
  Vector center;
  double radius;

  public PotentialField(Vector center, double radius) {
    this.center = center;
    this.radius = radius;
  }


  @Override
  public double getDensity(Vector v) {
    double l = v.minus(center).length();
    double d = 1.0;
    if ( l > 0 ) {
      d = (radius / (2 * l));
    } 
    return d;
  }


  @Override
  public Vector getDensityDerivative(Vector v) {
    Vector d = center.minus(v);
    double l = d.length();
    return d.times(radius / (l*l*l));
  }


  @Override
  public boolean getActive(Vector vector) {
    return true;
  }
  
}