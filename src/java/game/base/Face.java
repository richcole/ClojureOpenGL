package game.base;

import game.math.Matrix;
import game.math.Vector;

public class Face {
  
  Vector[] vertices;
  Vector[] colors;
  Vector   normal;
  Vector[] texturePoints;
  
  public Face() {
  }

  public Face(Vector[] vertices, Vector[] colors, Vector normal, Vector[] tps) {
    this.vertices = vertices;
    this.colors = colors;
    this.normal = normal;
    this.texturePoints = tps;
  }

  public Vector[] getVertices() {
    return vertices;
  }

  public Vector getNormal() {
    return normal;
  }

  public void setNormal(Vector normal) {
    this.normal = normal;
  }

  public void setVertices(Vector[] vertices) {
    this.vertices = vertices;
  }
  
  public Vector[] getTexturePoints() {
    return this.texturePoints;
  }

  public Vector[] getColors() {
    return colors;
  }

  public Face transform(Matrix tr) {
    return new Face(transform(vertices, tr), colors, transform(normal, tr), texturePoints);
  }

  private Vector[] transform(Vector[] vs, Matrix tr) {
    Vector[] res = new Vector[vs.length];
    for(int i=0;i<vs.length;++i) {
      res[i] = tr.times(vs[i]);
    }
    return res;
  }

  private Vector transform(Vector v, Matrix tr) {
    return tr.times(v);
  }
  
}
