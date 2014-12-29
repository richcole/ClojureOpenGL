package game.voxel;

import game.math.Vector;

public interface DensityFunction {

  boolean getActive(Vector vector);
  double getDensity(Vector vector);
  Vector getDensityDerivative(Vector vector); 

}
