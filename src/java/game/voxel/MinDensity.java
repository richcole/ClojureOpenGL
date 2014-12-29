package game.voxel;

import game.math.Vector;

import java.util.List;

import com.google.common.collect.Lists;

public class MinDensity implements DensityFunction {
  
  List<DensityFunction> fs;

  public MinDensity(DensityFunction ... fs) {
    this.fs = Lists.newArrayList(fs);
  }

  @Override
  public double getDensity(Vector v) {
    double np = 0, p = 0, nn = 0, n = 0;
    for(DensityFunction f: fs) {
      double d = f.getDensity(v);
      if ( d > 0.5 ) {
        p = np == 0 ? d : Math.min(d, p);
        np += 1;
      }
      else {
        n = nn == 0 ? d : Math.max(d, n);
        nn += 1;
      }
    }
    double r = np != 0 ? p : n;
    return r;
  }

  @Override
  public Vector getDensityDerivative(Vector v) {
    Vector p = Vector.Z;
    Vector n = Vector.Z;
    double np = 0, nn = 0, sp = 0, sn = 0;
    for(DensityFunction f: fs) {
      double d = f.getDensity(v);
      if ( Funs.isPos(d) ) {
        p = p.plus(f.getDensityDerivative(v).times(1/d));
        sp += 1/d;
      }
      else {
        n = n.plus(f.getDensityDerivative(v).times(1/d));
        sn += 1/d;
      }
    }
    return np != 0 ? p.times(1/sp) : n.times(1/sn);
  }

  @Override
  public boolean getActive(Vector vector) {
    return true;
  }

 
}