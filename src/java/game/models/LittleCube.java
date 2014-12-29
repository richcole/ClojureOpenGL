package game.models;

import game.Context;
import game.math.Vector;

public class LittleCube extends PhysicalCube {
  
  private static final double SIZE = 1;

  public LittleCube(Context context, Vector velocity, Vector position) {
    super(context, velocity, position, SIZE);
  }

}
