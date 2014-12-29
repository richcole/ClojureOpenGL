package game.voxel;

import game.math.Vector;

public class SplineField implements DensityFunction {
  
  float data[];
  int  mx, my, mz;
  
  static Vector dvs[] = {
    Vector.U1.plus(Vector.U2).plus(Vector.U3), 
    Vector.M1.plus(Vector.U2).plus(Vector.U3), 
    Vector.U1.plus(Vector.M2).plus(Vector.U3), 
    Vector.U1.plus(Vector.U2).plus(Vector.M3), 
    Vector.U1.plus(Vector.M2).plus(Vector.M3), 
    Vector.M1.plus(Vector.U2).plus(Vector.M3), 
    Vector.M1.plus(Vector.M2).plus(Vector.U3), 
    Vector.M1.plus(Vector.M2).plus(Vector.M3), 
  };
  
  public SplineField(Vector size) {
    mx = (int) size.x();
    my = (int) size.y();
    mz = (int) size.z();
    data = new float[mx*my*mz];
    for(int i=0;i<data.length;++i) {
      data[i] = 0f;
    }
  }
  
  int getIndex(Vector v) {
    int index = (int) ( (int)v.x() + ((int)v.y() * mx) + ((int)v.z() * mx * my) );
    if ( index >= data.length ) {
      throw new RuntimeException("Index out of bound " + index + " > " + data.length);
    }
    return index;
  }

  @Override
  public double getDensity(Vector v) {
    if ( inBounds(v) ) {
      Vector lb = new Vector((int)v.x(), (int)v.y(), (int)v.z(), 1);
      if ( lb.equals(v) ) {
        return data[getIndex(lb)];
      }
      double sv = 0;
      double sc = 0;
      for(Vector dv: dvs) {
        Vector p2 = lb.plus(dv);
        if ( inBounds(p2) ) {
          double l = p2.minus(v).length();
          sv += data[getIndex(p2)] * l;
          sc += l;
        }
      }
      return sv / sc;
    } else {
      return 0;
    }
  }

  private boolean inBounds(Vector v) {
    return (v.x() >= 0 && v.x() < mx) && (v.y() >= 0 && v.y() < my) && (v.z() >= 0 && v.z() < mz);
  }

  public void setDensity(Vector v, double d) {
    // d = Funs.clamp(0, 1, d);
    if ( inBounds(v)) {
      data[getIndex(v)] = (float)d;
    }
  }

  @Override
  public Vector getDensityDerivative(Vector v) {
    double d = getDensity(v);
    Vector sv = Vector.Z;
    for(Vector dv: dvs) {
      double d2 = getDensity(v.plus(dv));
      double dd = d2 - d;
      sv = sv.plus(dv.times(dd));
    }
    return sv.times(1.0/dvs.length);
  }
  
  public void addShape(Vector center, double radius, DensityFunction densityFunction) {
    Vector p1 = center.minus(Vector.ONES.times(radius));
    Vector p2 = center.plus(Vector.ONES.times(radius));
    for(int x=(int)p1.x();x<=p2.x();++x) {
      for(int y=(int)p1.y();y<=p2.y();++y) {
        for(int z=(int)p1.z();z<=p2.z();++z) {
          Vector p = new Vector(x, y, z, 1);
          double d1 = getDensity(p);
          double d2 = densityFunction.getDensity(p);
          setDensity(p, Funs.isPos(d2) ? 1 : 0);
        }
      }
    }
  }
  
  public void removeShape(Vector center, double radius, DensityFunction densityFunction) {
    Vector p1 = center.minus(Vector.ONES.times(radius));
    Vector p2 = center.plus(Vector.ONES.times(radius));
    for(int x=(int)p1.x();x<=p2.x();++x) {
      for(int y=(int)p1.y();y<=p2.y();++y) {
        for(int z=(int)p1.z();z<=p2.z();++z) {
          Vector p = new Vector(x, y, z, 1);
          double d1 = getDensity(p);
          double d2 = densityFunction.getDensity(p);
          if ( Funs.isPos(d2) ) {
            setDensity(p, 0);
          }
        }
      }
    }
  }
  
  public void smooth() {
    for(int x=0;x<mx;++x) {
      for(int y=0;y<my;++y) {
        for(int z=0;z<mz;++z) {
          Vector p = new Vector(x, y, z, 1);
          double d = getDensity(p);
          for(Vector dv: dvs) {
            d += (1/8.0) * getDensity(p.plus(dv));
          }
          setDensity(p, d);
        }
      }
    }
  }

  @Override
  public boolean getActive(Vector vector) {
    return true;
  }

}