package game.voxel;

import game.Context;
import game.math.Vector;

public class DeformableTerrain {

  private Context context;
  private SplineField s3;
  private Tessellation ts;
  private CubeMap cubeMap;
  private Vector topRight;
  private Vector center;
  private double radius;
  private double size;
  private PotentialField s1;
  private PotentialField s2;
  private ScaleTransform transform;

  public DeformableTerrain(Context context) {
    this.context = context;

    size = 20;
    topRight = Vector.ONES.times(size);
    center = topRight.times(0.5);
    radius = size * 0.25;
    s1 = new PotentialField(center, radius);
    s2 = new PotentialField(center.times(0.7), radius);
    s3 = new SplineField(Vector.ONES.times(size));
    s3.addShape(center, radius, s1);
    s3.removeShape(center.times(0.7), radius, s2);

    ts = new MarchingCubes();
    transform = new ScaleTransform(400/size);
    cubeMap = new CubeMap(context, Vector.Z, topRight, transform, s3, ts);
  }
  
  public void register() {
    cubeMap.register();
  }
  
  public void deform() {
    double r = size/6;
    Vector c = new Vector(size/2, size/2, 0, 1);
    s3.addShape(c, r, new PotentialField(c, r));
    ts.update(c.minus(Vector.ONES.times(radius)), c.plus(Vector.ONES.times(radius)), s3, transform);
    cubeMap.update();
  }
}
  
