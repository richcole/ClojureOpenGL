package game;

import game.base.SimObject;
import game.enums.TileSet;
import game.math.Matrix;
import game.math.Vector;
import game.models.Creature;
import game.models.Grid.TileSquare;
import game.models.LittleLight;
import game.models.TerrainTile;
import game.nwn.readers.set.SetReader.TileSetDescription;

import org.lwjgl.util.glu.GLU;

public class Player implements SimObject {
  
  static Vector LEFT     = Vector.LEFT;
  static Vector UP       = Vector.UP;
  static Vector NORMAL   = Vector.NORMAL;

  Vector pos;
  Vector left;
  Vector up;
  Vector normal;
  Vector velocity;
 
  double theta1, theta2;
  
  boolean movingDownward = false;
  boolean movingUpward = false;
  boolean movingForward = false;
  boolean movingBackward = false;
  boolean movingLeft = false;
  boolean movingRight = false;

  private Context context;
  
  public Player(Context context) {
    this.context = context;
    this.pos = new Vector(-100, -100, 150);
    this.theta1 = 45;
    this.theta2 = -45;
  }

  public Vector getLeft() {
    return left;
  }
  
  public Vector getUp() {
    return up;
  }

  public Vector getNormal() {
    return normal;
  }

  public synchronized void render() {
    Matrix rotUp = Matrix.rot(-theta1, UP);
    left = rotUp.times(LEFT);
    normal = rotUp.times(NORMAL);
    
    Matrix rotLeft = Matrix.rot(-theta2, left);
    normal = rotLeft.times(normal);
    up = rotLeft.times(UP);
    
    Vector p = pos.plus(velocity.times(context.getSimulator().getCurrentTickNibble()));
    Vector a = p.plus(getNormal());
    Vector u = getUp();
    GLU.gluLookAt((float)p.x(), (float)p.y(), (float)p.z(), (float)a.x(), (float)a.y(), (float)a.z(), (float)u.x(), (float)u.y(), (float)u.z());
    context.getSelectionRay().updateViewMatrix();
  }
  
  @Override
  public void tick() {
    velocity = Vector.Z;
    if ( movingForward ) {
      velocity = velocity.plus(getNormal());
    }
    if ( movingBackward ) {
      velocity = velocity.plus(getNormal().times(-1));
    }
    if ( movingLeft ) {
      velocity = velocity.plus(getLeft());
    }
    if ( movingRight ) {
      velocity = velocity.plus(getLeft().times(-1));
    }
    if ( movingUpward ) {
      velocity = velocity.plus(getUp());
    }
    if ( movingDownward ) {
      velocity = velocity.plus(getUp().times(-1));
    }
    float velocityScale = 2;
    velocity.scaleTo(velocityScale);
    pos = pos.plus(velocity);
  }

  public void setMovingDownward(boolean movingForward) {
    this.movingDownward = movingForward;
  }

  public void setMovingUpward(boolean movingForward) {
    this.movingUpward = movingForward;
  }

  public void setMovingForward(boolean movingForward) {
    this.movingForward = movingForward;
  }

  public void setMovingBackward(boolean movingBackward) {
    this.movingBackward = movingBackward;
  }

  public void setMovingLeft(boolean movingLeft) {
    this.movingLeft = movingLeft;
  }

  public void setMovingRight(boolean movingRight) {
    this.movingRight = movingRight;
  }

  public void fire() {
    Creature creature = context.newCreature();
    creature.register();
  }

  public void fireLight() {
    LittleLight littleLight = new LittleLight(context, getNormal().scaleTo(20), pos.plus(getNormal().scaleTo(10)));
    littleLight.register();
  }

  @Override
  public Vector getPos() {
    return pos;
  }

  @Override
  public double getMass() {
    return 100;
  }

  public void register() {
    context.getSimulator().register(this);
  }

  public void rotate(double dtheta1, double dtheta2) {
    theta1 += dtheta1 * 6.283f / 5000.0f;
    theta2 += dtheta2 * 6.283f / 5000.0f;
  }
  
  public double getTheta1() {
    return theta1;
  }
}
