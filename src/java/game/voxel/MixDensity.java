package game.voxel;

import game.math.Vector;

import java.util.List;

import com.google.common.collect.Lists;

public class MixDensity implements DensityFunction {
  
  List<DensityFunction> fs;

  public MixDensity(DensityFunction ... fs) {
    this.fs = Lists.newArrayList(fs);
  }

  @Override
  public double getDensity(Vector v) {
    double np = 0, p = 0, nn = 0, n = 0, sn = 0, sp = 0;
    for(DensityFunction f: fs) {
      double d = f.getDensity(v);
      if ( d == 0 ) {
        return 0;
      }
      else if ( d > 0 ) {
        p  += 1 / (d);
        sp += 1 / (d*d);
        np += 1;
      }
      else {
        n  += 1 / (d);
        sn += 1 / (d*d);
        nn += 1;
      }
    }
    double r = np != 0 ? p / sp : n / sn;
    return r;
  }

  @Override
  public Vector getDensityDerivative(Vector v) {
    Vector p = Vector.Z;
    Vector n = Vector.Z;
    double np = 0, nn = 0, sp = 0, sn = 0;
    for(DensityFunction f: fs) {
      double d = f.getDensity(v);
      if ( d == 0 ) {
        return f.getDensityDerivative(v);
      }
      else if ( Funs.isPos(d) ) {
        p = p.plus(f.getDensityDerivative(v).times(1/(d*d)));
        sp += 1/(d*d);
        np += 1;
      }
      else {
        n   = n.plus(f.getDensityDerivative(v).times(1/(d*d)));
        sn += 1/(d*d);
        nn += 1;
      }
    }
    return np != 0 ? p.times(1/sp) : n.times(1/sn);
  }

  @Override
  public boolean getActive(Vector vector) {
    return true;
  }

 
}