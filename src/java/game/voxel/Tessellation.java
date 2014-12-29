package game.voxel;

import game.math.Vector;
import game.proc.VertexCloud;

public interface Tessellation {
  public void update(Vector bottomLeft, Vector topRight, DensityFunction densityFunction, Transform transformation);
  public void setCloud(VertexCloud cloud);
}
