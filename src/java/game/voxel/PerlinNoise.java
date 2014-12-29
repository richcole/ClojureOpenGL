package game.voxel;

import java.util.Random;

import game.math.Vector;

public class PerlinNoise {
  
  Vector[] vs; 
  Random   random;
  int      tabMask;
  int      tabLen;
  
  public PerlinNoise(int logSize) {
    tabLen = 0x1 << (logSize-1);
    tabMask = tabLen - 1;
    vs = new Vector[tabLen];
    random = new Random();
    for(int i=0;i<vs.length;++i) {
      vs[i] = randomVector();
    }
  }

  private Vector randomVector() {
    Vector v = null;
    if ( v == null || v.lengthSquared() > 1 ) {
      v = new Vector(random.nextDouble(), random.nextDouble(), random.nextDouble(), 1);
    }
    return v.normalize();
  }
  
  
  private double gLattice(Vector i, Vector f) {
    int idx = index(i);
    return vs[idx].dot(f);
  }
  
  private int index(Vector u) {
    return perm(u.x()+perm(u.y()+perm(u.z())));
  }
  
  private int perm(double v) {
    return perm((int)(Math.floor(v)));    
  }
  
  private int perm(int v) {
    return v & tabMask;    
  }
  
  public double snoise(Vector x) {
    Vector i = x.floor();
    Vector f0 = x.minus(i);
    Vector f1 = f0.minus(Vector.ONES);
    Vector w = smoothStep(f0);
    
    double vx0, vx1, vy0, vy1, vz0, vz1;
    
    vx0 = gLattice(i, f0);
    vx1 = gLattice(i.plus(Vector.U1), new Vector(f1.x(), f0.y(), f0.z(), 1.0));
    vy0 = lerp(w.x(), vx0, vx1);
    
    vx0 = gLattice(i.plus(Vector.U2), new Vector(f0.x(), f1.y(), f0.z(), 1.0));
    vx1 = gLattice(i.plus(Vector.U1).plus(Vector.U2), new Vector(f1.x(), f1.y(), f0.z(), 1.0));
    vy1 = lerp(w.x(), vx0, vx1);

    vz0 = lerp(w.y(), vy0, vy1);
    vx0 = gLattice(i.plus(Vector.U3), new Vector(f0.x(), f0.y(), f1.z(), 1.0));
    vx1 = gLattice(i.plus(Vector.U1).plus(Vector.U3), new Vector(f1.x(), f0.y(), f1.z(), 1.0));
    vy0 = lerp(w.x(), vx0, vx1);
    
    vx0 = gLattice(i.plus(Vector.U2).plus(Vector.U3), new Vector(f0.x(), f1.y(), f1.z(), 1.0));
    vx1 = gLattice(i.plus(Vector.U1).plus(Vector.U2).plus(Vector.U3), new Vector(f1.x(), f1.y(), f1.z(), 1.0));
    vy1 = lerp(w.x(), vx0, vx1);
    vz1 = lerp(w.y(), vy0, vy1);
    
    return lerp(w.z(), vz0, vz1);
  }
  
  public double noise(Vector x) {
    double r = snoise(x);
    return (1.0 + r)/2.0;
  }

  public double turbulence(Vector x) {
    double r = 0;
    double fs = 0;
    for(double f=1;f<256;f*=2) {
      r += noise(x.times(f))/f;
      fs += 1/f;
    }
    return r / fs;
  }

  private double lerp(double t, double x0, double x1) {
    return x0 + t*(x1 - x0);
  }

  private Vector smoothStep(Vector f) {
    return new Vector(smoothStep(f.x()), smoothStep(f.y()), smoothStep(f.z()), 1);
  }

  private double smoothStep(double x) {
    return x*x*(3-2*x);
  }
  
}
