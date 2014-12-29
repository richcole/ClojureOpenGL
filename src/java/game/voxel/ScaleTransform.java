package game.voxel;

import game.math.Vector;

public class ScaleTransform implements Transform {

  private double scale;

  public ScaleTransform(double scale) {
    this.scale = scale;
  }

  @Override
  public Vector transform(Vector vector) {
    return vector.times(scale);
  }

  public Vector invTransform(Vector vector) {
    return vector.times(1/scale);
  }

  @Override
  public Vector transformNormal(Vector q1, Vector n1) {
    return n1;
  }
  
  @Override
  public Vector invTransformNormal(Vector q1, Vector n1) {
    return n1;
  }

  @Override
  public double transform(double scalar) {
    return scalar * scale;
  }

  @Override
  public double invTransform(double scalar) {
    return scalar / scale;
  }

}
