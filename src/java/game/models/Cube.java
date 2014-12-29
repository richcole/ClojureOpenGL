package game.models;

import game.Context;
import game.Material;
import game.Renderable;
import game.base.textures.TextureTile;
import game.math.Vector;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;


public class Cube implements Renderable {
  
  Context context;
  
  Vector up;
  Vector left;
  Vector pos;
  Vector normal;
  Material material;
  List<Rect> rects;
  List<TextureTile> textures;
   
  public Cube(Context context, Vector pos, double size) {
    this(context, pos, Vector.LEFT.scaleTo(size), Vector.UP.scaleTo(size), Vector.NORMAL.scaleTo(size));
  }
  
  public Cube(Context context, Vector pos, Vector left, Vector up, Vector normal) {
    this.context = context;
    this.pos = pos;
    this.left = left;
    this.up = up;
    this.normal = normal;
    this.material = context.getMaterial();
    this.textures = getTextures();
    this.rects = Lists.newArrayList(
      new Rect(normal,          up,            left,           getTexture(0)), // front
      new Rect(normal.minus(),  up,            left.minus(),   getTexture(1)), // back
      new Rect(left,            up,            normal.minus(), getTexture(3)), // left
      new Rect(left.minus(),    up,            normal,         getTexture(2)), // right
      new Rect(up,              left.minus(),  normal.minus(), getTexture(4)), // top
      new Rect(up.minus(),      left,          normal.minus(), getTexture(5))  // bottom
    );
  }

  private TextureTile getTexture(int i) {
    return textures.get(i % textures.size());
  }
  
  protected List<TextureTile> getTextures() {
    return Lists.newArrayList(context.getStoneTexture());
  }

  public void render() {
    GL11.glPushMatrix();
    GL11.glTranslated(pos.x(), pos.y(), pos.z());
    material.render();
    for(Rect rect: rects) {
      rect.render();
    }
    GL11.glPopMatrix();
  }

  public void setPos(Vector pos) {
    this.pos = pos;
  }
}
