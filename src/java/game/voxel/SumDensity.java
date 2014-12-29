package game.voxel;

import game.math.Vector;

import java.util.List;

import com.google.common.collect.Lists;

public class SumDensity implements DensityFunction {
  
  List<DensityFunction> fs;

  public SumDensity(DensityFunction ... fs) {
    this.fs = Lists.newArrayList(fs);
  }

  @Override
  public double getDensity(Vector v) {
    double r = 0;
    for(DensityFunction f: fs) {
      r += f.getDensity(v);
    }
    return r;
  }

  @Override
  public Vector getDensityDerivative(Vector v) {
    Vector r = Vector.Z;
    for(DensityFunction f: fs) {
      r = r.plus(f.getDensityDerivative(v));
    }
    return r;
  }

  @Override
  public boolean getActive(Vector vector) {
    return true;
  }

 
}