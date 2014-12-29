package game.models;

import game.Context;
import game.Renderable;
import game.base.PhysicalObject;
import game.math.Vector;

public class PhysicalCube extends PhysicalObject implements Renderable {
  
  Cube cube;

  PhysicalCube(Context context, Vector velocity, Vector position, double size) {
    super(context, velocity, position, size);
    this.cube = new Cube(context, position, size);
  }

  @Override
  public void move(Vector velocity) {
    super.move(velocity);
    cube.setPos(getPos());
  }

  @Override
  public void render() {
    cube.setPos(getPos());
    cube.render();
  }
  
  public void register() {
    super.register();
    context.getScene().register(this);
  }
  
}
