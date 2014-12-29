package game.voxel;

import game.math.Vector;

public class PerlinNoiseField implements DensityFunction {

  PerlinNoise noise = new PerlinNoise(8);
  double scale;

  public PerlinNoiseField(double scale) {
    this.scale = scale;
  }


  @Override
  public double getDensity(Vector v) {
    return noise.turbulence(v.times(1/scale));
  }


  @Override
  public Vector getDensityDerivative(Vector v) {
    return Vector.Z;
  }


  @Override
  public boolean getActive(Vector vector) {
    return true;
  }
  
}