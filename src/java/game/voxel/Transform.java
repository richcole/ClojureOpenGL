package game.voxel;

import game.math.Vector;

public interface Transform {

  double transform(double scalar);
  double invTransform(double scalar);
  
  Vector transform(Vector vector);
  Vector invTransform(Vector vector);

  Vector invTransformNormal(Vector q1, Vector n1);
  Vector transformNormal(Vector q1, Vector n1);

}
