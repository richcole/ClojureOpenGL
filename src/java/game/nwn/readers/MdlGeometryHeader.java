package game.nwn.readers;

public class MdlGeometryHeader {
  long[] aulGeomRoutines;
  String name;
  MdlNodeHeader geometry;
  Long nodeCount;
  long[] rtArray1;
  long[] rtArray2;
  long  u2;
  long geomType;
  
  public MdlNodeHeader getGeometry() {
    return geometry;
  }
  
  public void setGeometry(MdlNodeHeader geometry) {
    this.geometry = geometry;
  }

  public String getName() {
    return name;
  }
}