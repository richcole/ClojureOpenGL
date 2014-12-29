package game.nwn.readers;

import game.math.Vector;

public class MdlFace {
  public static final int SIZE = 0x20;
  Vector planeNormal;
  float planeDistance;
  long  surface;
  int[] adjFace;
  int[] vertex;
  public Vector getPlaneNormal() {
    return planeNormal;
  }
  public void setPlaneNormal(Vector planeNormal) {
    this.planeNormal = planeNormal;
  }
  public float getPlaneDistance() {
    return planeDistance;
  }
  public void setPlaneDistance(float planeDistance) {
    this.planeDistance = planeDistance;
  }
  public long getSurface() {
    return surface;
  }
  public void setSurface(long surface) {
    this.surface = surface;
  }
  public int[] getAdjFace() {
    return adjFace;
  }
  public void setAdjFace(int[] adjFace) {
    this.adjFace = adjFace;
  }
  public int[] getVertex() {
    return vertex;
  }
  public void setVertex(int[] vertex) {
    this.vertex = vertex;
  }
  public static int getSize() {
    return SIZE;
  }
}