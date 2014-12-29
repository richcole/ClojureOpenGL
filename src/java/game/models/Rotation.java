package game.models;

import game.math.Vector;

import org.lwjgl.opengl.GL11;

public class Rotation {
  
  Vector axis;
  double angle;
  
  public Rotation(double angle, Vector axis) {
    super();
    this.axis = axis;
    this.angle = angle;
  }

  public Vector getAxis() {
    return axis;
  }

  public void setAxis(Vector axis) {
    this.axis = axis;
  }

  public double getAngle() {
    return angle;
  }

  public void setAngle(double angle) {
    this.angle = angle;
  }
  
  public void render() {
    GL11.glRotated(angle, axis.x(), axis.y(), axis.z());
  }
  

}
