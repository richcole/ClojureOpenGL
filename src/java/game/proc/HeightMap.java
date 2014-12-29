package game.proc;

import game.Context;
import game.Renderable;
import game.base.textures.TextureTile;
import game.math.Vector;
import game.utils.GLUtils;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class HeightMap implements Renderable {
  
  static private Logger logger = Logger.getLogger(HeightMap.class); 
  
  int width;
  int depth;
  Vector normals[];
  Vector pos[];
  double scale = 10;

  private Context context;
  
  public HeightMap(Context context, int width, int depth) {
    this.context = context;
    this.width = width;
    this.depth = depth;
    this.pos = new Vector[width*depth];
    this.normals = new Vector[width*depth];
  }
  
  public void set(int x, int y, double h) {
    int i = getIndex(x,y);
    pos[i] = new Vector(x, y, h, 1);
  }

  public void setIfNotSet(int x, int y, double h) {
    int i = getIndex(x,y);
    if ( pos[i] == null ) {
      pos[i] = new Vector(x, y, h, 1);
    }
  }

  public int getIndex(int x, int y) {
    if ( x >= width || y >= depth ) {
      throw new RuntimeException("Invalid coords x=" + x + " y=" + y);
    }
    return (y*width) + x;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public double get(int x, int y) {
    int i = getIndex(x, y);
    if ( pos[i] != null ) {
      return pos[i].z();
    } else {
      return 0.0;
    }
  }
  
  public void calculateNormals() {
    for(int x=0;x<width; ++x) {
      normals[getIndex(x, 0)] = new Vector(0, 0, 1, 1);
      normals[getIndex(x, depth-1)] = new Vector(0, 0, 1, 1);
    }
    for(int y=0;y<depth; ++y) {
      normals[getIndex(0, y)] = new Vector(0, 0, 1, 1);
      normals[getIndex(width-1, y)] = new Vector(0, 0, 1, 1);
    }
    for(int x=1;x<width-1; ++x) {
      for(int y=1;y<depth-1; ++y) {
        Vector a = new Vector(1, 0, get(x+1, y)-get(x+1, y), 1);
        Vector b = new Vector(0, 1, get(x, y+1)-get(x, y-1), 1);
        normals[getIndex(x,y)] = a.cross(b).normalize();
      }
    }
  }

  @Override
  public void render() {
    TextureTile texture = context.getGrassTexture();
    texture.bind();
    double z = texture.getTextureZ();
    
    GL11.glBegin(GL11.GL_TRIANGLES);
    double w = width;
    double d = depth;
    for(int x=1;x<width-1; ++x) {
      for(int y=1;y<depth-1; ++y) {
        // top left
        vertex(z, w, d, x, y, -1, 0);
        vertex(z, w, d, x, y, 0,  -1);
        vertex(z, w, d, x, y, 0,  0);

        // top right
        vertex(z, w, d, x, y, 0, -1);
        vertex(z, w, d, x, y, 1,  0);
        vertex(z, w, d, x, y, 0,  0);

        // bottom left
        vertex(z, w, d, x, y, -1, 0);
        vertex(z, w, d, x, y,  0, 1);
        vertex(z, w, d, x, y,  0, 0);

        // bottom right
        vertex(z, w, d, x, y,  1, 0);
        vertex(z, w, d, x, y,  0, 1);
        vertex(z, w, d, x, y,  0, 0);
      }
    }
    GL11.glEnd();
  }

  private void vertex(double tz, double w, double d, int x, int y, int dx, int dy) {
    GL11.glTexCoord3d((x+dx)/w, (y+dy)/d, tz);
    GLUtils.glVertex(getPosition((x+dx), (y+dy)));
    GLUtils.glNormal(getNormal((x+dx), (y+dy)));
  }

  private Vector getPosition(int x, int y) {
    return pos[getIndex(x,y)].times(scale);
  }

  private Vector getNormal(int x, int y) {
    return normals[getIndex(x, y)];
  }

  private Vector getTexture(int x, int y) {
    return new Vector(x/(double)width, y/(double)depth, 0, 1);
  }

  public void register() {
    context.getScene().register(this);
  }

  public void copyTo(VertexCloud vCloud) {
    for(int x=1;x<width-1; ++x) {
      for(int y=1;y<depth-1; ++y) {
        // top left
        vCloud.addVertex(getPosition(x-1, y), getNormal(x-1, y), getTexture(x-1, y));
        vCloud.addVertex(getPosition(x, y-1), getNormal(x, y-1), getTexture(x, y-1));
        vCloud.addVertex(getPosition(x, y),   getNormal(x-1, y), getTexture(x, y));

        // top right
        vCloud.addVertex(getPosition(x, y-1), getNormal(x, y-1), getTexture(x, y-1));
        vCloud.addVertex(getPosition(x+1, y), getNormal(x+1, y), getTexture(x+1, y));
        vCloud.addVertex(getPosition(x, y), getNormal(x, y), getTexture(x, y));

        // bottom left
        vCloud.addVertex(getPosition(x-1, y), getNormal(x-1, y), getTexture(x-1, y));
        vCloud.addVertex(getPosition(x, y+1), getNormal(x, y+1), getTexture(x, y+1));
        vCloud.addVertex(getPosition(x, y), getNormal(x, y), getTexture(x, y));

        // bottom right
        vCloud.addVertex(getPosition(x+1, y), getNormal(x+1, y), getTexture(x+1, y));
        vCloud.addVertex(getPosition(x, y+1), getNormal(x, y+1), getTexture(x, y+1));
        vCloud.addVertex(getPosition(x, y), getNormal(x, y), getTexture(x, y));
      }
    }
  }
}
