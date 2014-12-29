package game.models;

import game.Renderable;
import game.math.Vector;

import org.lwjgl.opengl.GL11;

public class Line implements Renderable {
  
  Vector position;
  Vector size;
  float  width;
  
  Line(Vector position, Vector size, float width) {
    this.position = position;
    this.size = size;
  }

  @Override
  public void render() {
    GL11.glLineWidth((float) width); 
    GL11.glColor3f(1.0f, 0.0f, 0.0f);
    GL11.glBegin(GL11.GL_LINES);
    GL11.glVertex3d(position.x(), position.y(), position.z());
    GL11.glVertex3d(position.x() + size.x(), position.y() + size.y(), position.z() + size.z());
    GL11.glEnd();
  }

}
