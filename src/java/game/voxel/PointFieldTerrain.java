package game.voxel;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import game.math.Vector;
import game.models.Cube;

public class PointFieldTerrain {
  
  
  static private class PointDensity {
    double r;
    Vector p;
    
    public PointDensity(double r, Vector p) {
      super();
      this.r = r;
      this.p = p;
    }
  }
    
  List<PointDensity> pds = Lists.newArrayList();
  Set<Cube> cubes;  
  double grain = 10;
  
  public double density(Vector p) {
    double density = 0;
    for(PointDensity pd: pds) {
      density += 0.5 * pd.r * pd.r / p.minus(pd.p).lengthSquared();
    }
    return density;
  }
  
  public void addPoint(double r, Vector p) {
    pds.add(new PointDensity(r, p));
    Vector bl = p.minus(Vector.ONES.times(r)).modulo(grain);
    Vector tr = p.plus(Vector.ONES.times(r)).modulo(grain);
    for(double x=bl.x(); x<tr.x(); x += grain) {
      for(double y=bl.x(); y<tr.x(); y += grain) {
        for(double z=bl.x(); z<tr.x(); z += grain) {
           Vector c = new Vector(x, y, z, 1.0);
           
        }
      }
    }
  }
  

}
