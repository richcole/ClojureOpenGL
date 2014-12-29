package game.nwn.readers;


public class MdlAnimation {
  MdlGeometryHeader geometryHeader;
  float length;
  float transTime;
  String animRoot;
  MdlAnimationEvent[] events;
  
  public MdlGeometryHeader getGeometryHeader() {
    return geometryHeader;
  }
  
  public void setGeometryHeader(MdlGeometryHeader geometryHeader) {
    this.geometryHeader = geometryHeader;
  }
  
  public float getLength() {
    return length;
  }
  
  public void setLength(float length) {
    this.length = length;
  }
  
  public float getTransTime() {
    return transTime;
  }
  
  public void setTransTime(float transTime) {
    this.transTime = transTime;
  }
  
  public String getAnimRoot() {
    return animRoot;
  }
  
  public void setAnimRoot(String animRoot) {
    this.animRoot = animRoot;
  }
  
  public MdlAnimationEvent[] getEvents() {
    return events;
  }
  
  public void setEvents(MdlAnimationEvent[] events) {
    this.events = events;
  }
}