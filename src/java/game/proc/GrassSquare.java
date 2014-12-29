package game.proc;

import game.Context;
import game.Renderable;
import game.base.textures.TextureTile;
import game.math.Matrix;
import game.math.Vector;
import game.models.Rect;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class GrassSquare implements Renderable {
  
  public static final class RectDistance implements Comparator<Rect> {
    @Override
    public int compare(Rect o1, Rect o2) {
      return - Double.compare(o1.getDistance(), o2.getDistance());
    }
  }

  Context context;
  GrassImage grassImage;
  Vector pos;
  double offsets[]; 
  Rect rects[];
  
  public GrassSquare(Context context) {
    this.context = context;
    this.grassImage = new GrassImage(context);
    this.pos  = Vector.UP.times(20);
    this.offsets = new  double[64];
    Random random = new Random();
    for(int i=0;i<offsets.length;++i) {
      offsets[i] = random.nextDouble() - 0.5;
    }
    TextureTile textureTile = grassImage.getTextureTile();
    Vector p = Vector.UP.times(20);
    double scale = 50;
    int N = 20;
    rects = new Rect[N*N*3];
    Vector l = Vector.LEFT.times(scale/2);
    Vector n = Vector.NORMAL.times(scale/3);
    Matrix m1 = Matrix.rot(2 * Math.PI / 3, Vector.UP);
    Matrix m2 = Matrix.rot(4 * Math.PI / 3, Vector.UP);
    Vector[] ls = { l, l.times(m1), l.times(m2) };
    Vector[] ps = { n, n.times(m1), n.times(m2) };
    for(int x=0;x<N;++x) {
      for(int y=0;y<N;++y) {
        for(int a=0;a<3;++a) {
          Vector dx = Vector.LEFT.times(- scale * x);
          Vector dy = Vector.NORMAL.times(scale*y);
          Rect rect = new Rect(p.plus(dx).plus(dy).minus(ps[a]), Vector.UP.times(scale/2), ls[a], textureTile);
          rect.setNormal(Vector.UP);
          rects[(y*N+x)*3+a] = rect;
        }
      }
    }
  }

  @Override
  public void render() {
    Vector p = context.getPlayer().getPos();
    Vector n = context.getPlayer().getNormal();
    for(Rect rect: rects) {
      // double maxDistance = Math.max(rect.getBottomLeft().minus(p).dot(n), rect.getBottomRight().minus(p).dot(n));
      rect.setDistance(rect.getPos().minus(p).dot(n));
    }
    Arrays.sort(rects, new RectDistance());
    for(Rect rect: rects) {
      rect.render();
    }
  }
  
  public void register() {
    context.getScene().register(this);
  }

}
