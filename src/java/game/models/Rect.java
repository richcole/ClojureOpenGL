package game.models;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import game.Renderable;
import game.base.textures.TextureTile;
import game.math.Vector;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

public class Rect implements Renderable {
  
  Vector pos;
  Vector normal;
  Vector up;
  Vector left;
  TextureTile textureTile;
  List<Vector> textureCoords;
  double distance;
  Vector[] corners;
  
  public Rect(Vector pos, Vector up, Vector left, TextureTile textureTile) {
    this.pos = pos;
    this.up = up;
    this.left = left;
    this.normal = left.cross(up).scaleTo(1.0);
    this.textureTile = textureTile;
    this.textureCoords = Lists.newArrayList(
      new Vector(0,    0,   0.0, 1.0),  
      new Vector(1.0,  0,   0.0, 1.0),  
      new Vector(1.0,  1.0, 0.0, 1.0),  
      new Vector(0,    1.0, 0.0, 1.0)  
    );
    Vector[] c = {
      pos.plus(up).plus(left), pos.plus(up).minus(left), pos.minus(up).minus(left), pos.minus(up).plus(left)   
    };
    this.corners = c;
  }

  @Override
  public void render() {
    GL11.glColor3d(1.0f, 1.0f, 1.0f);
    textureTile.bind();
    GL11.glBegin(GL_QUADS);
    for(int i=0;i<4;++i) {
      Vector v = corners[i];
      Vector t = textureCoords.get(i);
      GL11.glTexCoord3d(t.x(), t.y(), textureTile.getTextureZ());
      GL11.glNormal3d(normal.x(), normal.y(), normal.z());
      GL11.glVertex3d(v.x(), v.y(), v.z());
    }
    GL11.glEnd();
    
  }

  public void setPos(Vector pos) {
    this.pos = pos;
  }
  
  public void setNormal(Vector normal) {
    this.normal = normal;
  }

  public Vector getPos() {
    return this.pos;
  }

  public Vector getBottomLeft() {
    return this.pos.plus(left).minus(up);
  }

  public Vector getBottomRight() {
    return this.pos.minus(left).minus(up);
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public double getDistance() {
    return distance;
  }

}
