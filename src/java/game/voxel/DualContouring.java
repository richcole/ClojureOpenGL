package game.voxel;

import game.math.Vector;
import game.proc.VertexCloud;

import java.util.Map;

import Jama.Matrix;

import com.google.common.collect.Maps;

public class DualContouring implements Tessellation {
  
  final static Vector[] UNITS = {
    new Vector(  1,   0,   0,  1), // 0
    new Vector(  0,   1,   0,  1), // 1
    new Vector(  0,   0,   1,  1), // 2
    new Vector( -1,   0,   0,  1), // 0
    new Vector(  0,  -1,   0,  1), // 1
    new Vector(  0,   0,  -1,  1), // 2
  };
  
  final static Vector[] DIRNS = {
    new Vector( 1,  0,  0,  1), // 0
    new Vector( 0,  1,  0,  1), // 1
    new Vector( 0,  0,  1,  1), // 2
    new Vector( -1,  0,  0,  1), // 0
    new Vector( 0,  -1,  0,  1), // 1
    new Vector( 0,  0,  -1,  1), // 2
  };

  final static Vector[] VERTICIES = {
    new Vector(-1, -1, -1, 1), // 0
    
    new Vector( 1, -1, -1, 1), // 1
    new Vector(-1,  1, -1, 1), // 2
    new Vector(-1, -1,  1, 1), // 3
    
    new Vector(-1,  1,  1, 1), // 4
    new Vector( 1, -1,  1, 1), // 5
    new Vector( 1,  1, -1, 1), // 6
    
    new Vector( 1,  1,  1, 1)  // 7
  };
  
  final static int[][] EDGES = {
    {0, 1},
    {0, 2},
    {0, 3},

    {4, 7},
    {4, 2},
    {4, 3},
    
    {5, 7},
    {5, 1},
    {5, 3},
    
    {6, 7},
    {6, 1},
    {6, 2},
  };

  static final int[][] EDGE_UNITS = new int[EDGES.length][];
  static final boolean[] DRAW_EDGE = new boolean[EDGES.length];
  
  static {
    
    for(int i=0;i<EDGES.length;++i) {
      EDGE_UNITS[i] = new int[2];
      int k = 0;
      for(int j=0;j<3;++j) {
        double c1 = VERTICIES[EDGES[i][0]].get(j);
        double c2 = VERTICIES[EDGES[i][1]].get(j);
        if ( c1 == c2 ) {
          if ( c1 > 0 ) {
            EDGE_UNITS[i][k++] = j;
          } else {
            EDGE_UNITS[i][k++] = 3+j;
          }
        }
      }
    }
    
    for(int i=0;i<EDGES.length;++i) {
      int u1 = EDGE_UNITS[i][0];
      int u2 = EDGE_UNITS[i][1];
      DRAW_EDGE[i] =  u1 < 3 && u2 < 3 || u1 >= 3 && u2 >= 3 ;
    }
    
    int x = 1;
  }

  private VertexCloud cloud;
  
  public DualContouring() {
  }
  
  public void setCloud(VertexCloud cloud) {
    this.cloud = cloud;
  }
  
  @Override
  public void update(Vector bottomLeft, Vector topRight, DensityFunction densityFunction,
    Transform tr) {
    double grid[] = new double[8];
    Vector vs[] = new Vector[12];
    Vector ev[] = new Vector[12];
    Vector edv[] = new Vector[12];
    Map<Vector, Vector> vm = Maps.newHashMap();
    Map<Vector, Vector> vn = Maps.newHashMap();
    
    for (double x = bottomLeft.x(); x < topRight.x(); ++x) {
      for (double y = bottomLeft.y(); y < topRight.y(); ++y) {
        for (double z = bottomLeft.z(); z < topRight.z(); ++z) {
          Vector p = new Vector(x, y, z, 1);
          for(int i=0; i<VERTICIES.length; ++i) {
            vs[i] = p.plus(VERTICIES[i].times(0.5));
            grid[i] = densityFunction.getDensity(vs[i]);
          }
          
          int evIndex = 0;
          for(int i=0; i<EDGES.length; ++i) {
            double d1 = grid[EDGES[i][0]];
            double d2 = grid[EDGES[i][1]];
            if ( Funs.isPos(d1) != Funs.isPos(d2) ) {
              Vector mp = Funs.midPoint(d1, d2, vs[EDGES[i][0]], vs[EDGES[i][1]]);
              ev[evIndex] = mp;
              edv[evIndex] = densityFunction.getDensityDerivative(mp);
              evIndex++;
            }
          }
          
          // solve for ev
          solveCubePoint(densityFunction, ev, edv, vm, vn, p, evIndex);
          
        }
      }
    }
    
    for (double x = bottomLeft.x(); x < topRight.x(); ++x) {
      for (double y = bottomLeft.y(); y < topRight.y(); ++y) {
        for (double z = bottomLeft.z(); z < topRight.z(); ++z) {
          Vector p = new Vector(x, y, z, 1);
          for(int i=0; i<VERTICIES.length; ++i) {
            vs[i] = p.plus(VERTICIES[i].times(0.5));
            grid[i] = densityFunction.getDensity(vs[i]);
          }
          
          Vector p1 = new Vector(x, y, z, 1);
          Vector q1 = vm.get(p1);
          Vector n1 = vn.get(p1);
          if ( q1 != null ) {
            for(int i=0; i<EDGES.length; ++i) {
              if ( DRAW_EDGE[i] ) {
                double d1 = grid[EDGES[i][0]];
                double d2 = grid[EDGES[i][1]];
                if ( Funs.isPos(d1) != Funs.isPos(d2) ) {
                  Vector u1 = UNITS[EDGE_UNITS[i][0]];
                  Vector u2 = UNITS[EDGE_UNITS[i][1]];
                  Vector q2 = vm.get(p1.plus(u1));
                  Vector q3 = vm.get(p1.plus(u2));
                  Vector n2 = vn.get(p1.plus(u1));
                  Vector n3 = vn.get(p1.plus(u2));
                  if ( q1 != null && q2 != null && q3 != null ) {
                    cloud.addVertex(tr.transform(q1), tr.transformNormal(q1, n1), Vector.Z);
                    cloud.addVertex(tr.transform(q2), tr.transformNormal(q2, n2), Vector.Z);
                    cloud.addVertex(tr.transform(q3), tr.transformNormal(q3, n3), Vector.Z);
                  }
                }
              }
            }
          }
        }      
      }
    }
    // cloud.computeNormals();
  }

  private void solveCubePoint1(DensityFunction densityFunction, Vector[] ev, Vector[] edv, Map<Vector, Vector> vm,
    Map<Vector, Vector> vn, Vector p, int evIndex) {
    if ( evIndex > 0 ) {
      Matrix A = new Matrix(evIndex, 3);
      Matrix b = new Matrix(evIndex, 1);
      for(int i=0;i<evIndex;++i) {
        A.set(i, 0, edv[i].x());
        A.set(i, 1, edv[i].y());
        A.set(i, 2, edv[i].z());
        b.set(i, 0, ev[i].dot(edv[i]));
      }
      
      try {
        Matrix s = A.solve(b);
        Vector sv = new Vector(s.get(0, 0), s.get(1, 0), s.get(2, 0), 1);
        if ( p.minus(sv).length() > 1 ) {
          sv = p;
        }
        vm.put(p, sv);
        vn.put(p, densityFunction.getDensityDerivative(sv).normalize());
      } catch(RuntimeException e) {
        
      }
    }
  }

  private void solveCubePoint2(DensityFunction densityFunction, Vector[] ev, Vector[] edv, Map<Vector, Vector> vm,
    Map<Vector, Vector> vn, Vector p, int evIndex) {
    if ( evIndex > 0 ) {
      Vector sv1 = Vector.Z;
      for(int i=0;i<evIndex;++i) {
        sv1 = sv1.plus(ev[i]);
      }
      sv1 = sv1.times(1.0/evIndex);
      double d = densityFunction.getDensity(sv1);
      Vector dp = densityFunction.getDensityDerivative(sv1);
      Vector adj = Vector.ONES.times(d).divide(dp);
      Vector sv2 = sv1.minus(adj);
      double d1 = densityFunction.getDensity(sv1);
      double d2 = densityFunction.getDensity(sv2);
      if ( Math.abs(d1 - 0.5) < Math.abs(d2 - 0.5) || p.minus(sv2).length() > 1 ) {
        sv2 = sv1;
      }
      vm.put(p, sv2);
      vn.put(p, densityFunction.getDensityDerivative(sv2).normalize());
    }
  }

  private void solveCubePoint(DensityFunction densityFunction, Vector[] ev, Vector[] edv, Map<Vector, Vector> vm,
    Map<Vector, Vector> vn, Vector p, int evIndex) {
    if ( evIndex > 0 ) {
      Vector sv1 = Vector.Z;
      for(int i=0;i<evIndex;++i) {
        sv1 = sv1.plus(ev[i]);
      }
      sv1 = sv1.times(1.0/evIndex);
      vm.put(p, sv1);
      vn.put(p, densityFunction.getDensityDerivative(sv1).normalize());
    }
  }

}
