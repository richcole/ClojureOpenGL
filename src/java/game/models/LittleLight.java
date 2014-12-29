package game.models;

import game.Context;
import game.base.PhysicalObject;
import game.math.Vector;

public class LittleLight extends PhysicalObject {
  
  private static final double SIZE = 1;
  
  Light light;
  Cube  cube;

  public LittleLight(Context context, Vector velocity, Vector position) {
    super(context, velocity, position, SIZE);
    light = new Light(context, position, context.getNextLightNumber());
    cube = new Cube(context, position, Vector.U1.scaleTo(SIZE), Vector.U2.scaleTo(SIZE), Vector.U3.scaleTo(SIZE));
  }

  @Override
  public void move(Vector velocity) {
    super.move(velocity);
  }
}
