package game.nwn.readers;

import java.util.List;
import java.util.Map;

public class MdlModel {
  private MdlGeometryHeader geometryHeader;
  int       aucFlags; 
  long      classification;
  int       fog;
  long      refCount;
  List<MdlAnimation> animations;
  MdlModel  superModel;
  long[]    bb;
  float     radius;
  float     animScale;
  String    superModelName;
  
  Map<String, MdlAnimation> animMap;
  
  public Map<String, MdlAnimation> getAnimMap() {
    return animMap;
  }

  public void setAnimMap(Map<String, MdlAnimation> animMap) {
    this.animMap = animMap;
  }

  public MdlGeometryHeader getGeometryHeader() {
    return geometryHeader;
  }
  
  public void setGeometryHeader(MdlGeometryHeader geometryHeader) {
    this.geometryHeader = geometryHeader;
  }

  public int getAucFlags() {
    return aucFlags;
  }

  public void setAucFlags(int aucFlags) {
    this.aucFlags = aucFlags;
  }

  public long getClassification() {
    return classification;
  }

  public void setClassification(long classification) {
    this.classification = classification;
  }

  public int getFog() {
    return fog;
  }

  public void setFog(int fog) {
    this.fog = fog;
  }

  public long getRefCount() {
    return refCount;
  }

  public void setRefCount(long refCount) {
    this.refCount = refCount;
  }

  public List<MdlAnimation> getAnimations() {
    return animations;
  }

  public void setAnimations(List<MdlAnimation> animations) {
    this.animations = animations;
  }

  public MdlModel getSuperModel() {
    return superModel;
  }

  public void setSuperModel(MdlModel superModel) {
    this.superModel = superModel;
  }

  public long[] getBb() {
    return bb;
  }

  public void setBb(long[] bb) {
    this.bb = bb;
  }

  public float getRadius() {
    return radius;
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }

  public float getAnimScale() {
    return animScale;
  }

  public void setAnimScale(float animScale) {
    this.animScale = animScale;
  }

  public String getSuperModelName() {
    return superModelName;
  }

  public void setSuperModelName(String superModelName) {
    this.superModelName = superModelName;
  }
  
}