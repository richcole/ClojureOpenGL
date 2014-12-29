package game.voxel;

import game.Context;
import game.Registerable;
import game.Renderable;
import game.math.Vector;
import game.proc.VertexCloud;
import game.shaders.ProgramRenderer;

public class CubeMap implements Renderable, Registerable {
  
  VertexCloud cloud;
  Context context;
  ProgramRenderer program;
  
  public CubeMap(Context context, Vector bottomLeft, Vector topRight, Transform transform, DensityFunction densityFunction, Tessellation ts) {
    this.context = context;
    this.cloud = new VertexCloud();
    program = new ProgramRenderer(context, cloud, "screen");

    ts.setCloud(cloud);
    ts.update(bottomLeft, topRight, densityFunction, transform);
    cloud.freeze();
  }
  
  @Override
  public void render() {
    program.render();
  }

  @Override
  public void register() {
    context.getScene().register(this);
  }
  
  public void update() {
    cloud.freeze();
  }

}
