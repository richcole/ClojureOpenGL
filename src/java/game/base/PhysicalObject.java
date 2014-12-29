package game.base;

import game.Context;
import game.math.Vector;

public class PhysicalObject implements SimObject {
  
  protected Context context;
  protected Vector velocity;
  protected Vector pos;
  protected double mass;
  
  protected PhysicalObject(Context context, Vector velocity, Vector pos, double mass) {
    this.context = context;
    this.velocity = new Vector(velocity);
    this.pos = pos;
    this.mass = mass;
  }

  @Override
  public void tick() {
    velocity = velocity.plus(getPos().minus().times(0.0001));
    for(SimObject o: context.getSimulator().getSimObjects()) {
      velocity = velocity.plus(collisionForce(o));
    }
    if ( velocity.length() > 1 ) {
      move(velocity);
      velocity = velocity.times(0.95f);
    }
  }
  
  public Vector collisionForce(SimObject o) {
    if ( o != this ) {
      double r  = o.getMass();
      Vector v  = getPos().minus(o.getPos());
      double vls = v.lengthSquared(); 
      if ( vls < 1 ) {
        vls = 1;
      }
      return v.scaleTo(r/vls);
    }
    else {
      return Vector.Z;
    }
  }
  
  public void move(Vector velocity) {
    pos = pos.plus(velocity);
  }

  @Override
  public Vector getPos() {
    return pos;
  }
  
  @Override
  public double getMass() {
    return mass;
  }
  
  public void register() {
    context.getSimulator().register(this);    
  }

}
