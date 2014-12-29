package game.nwn.readers;

import game.math.Quaternion;
import game.math.Vector;


public class MdlNodeHeader {
  public static final int SIZE = 0x70;
  long[] nodeRoutines;
  long inheritColor;
  long partNumber;
  String name;
  MdlGeometryHeader geomemtryHeader;
  MdlNodeHeader parentNode;
  MdlNodeHeader[] children;
  MdlControllerKey[] controllerKey;
  float[] controllerData;
  long flags;
  MdlNodeType nodeType;
  MdlMeshHeader meshHeader;
  MdlReferenceNode referenceNode;
  MdlAnimMeshNode animMeshNode;
  
  Vector position[];
  Quaternion orientation[];
  float scale[];
  public float[] positionTimings;
  public float[] orientationTimings;
    
  public MdlNodeHeader[] getChildren() {
    return children;
  }
  
  public void setChildren(MdlNodeHeader[] children) {
    this.children = children;
  }

  public long[] getNodeRoutines() {
    return nodeRoutines;
  }

  public void setNodeRoutines(long[] nodeRoutines) {
    this.nodeRoutines = nodeRoutines;
  }

  public long getInheritColor() {
    return inheritColor;
  }

  public void setInheritColor(long inheritColor) {
    this.inheritColor = inheritColor;
  }

  public long getPartNumber() {
    return partNumber;
  }

  public void setPartNumber(long partNumber) {
    this.partNumber = partNumber;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MdlGeometryHeader getGeomemtryHeader() {
    return geomemtryHeader;
  }

  public void setGeomemtryHeader(MdlGeometryHeader geomemtryHeader) {
    this.geomemtryHeader = geomemtryHeader;
  }

  public MdlNodeHeader getParentNode() {
    return parentNode;
  }

  public void setParentNode(MdlNodeHeader parentNode) {
    this.parentNode = parentNode;
  }

  public MdlControllerKey[] getControllerKey() {
    return controllerKey;
  }

  public void setControllerKey(MdlControllerKey[] controllerKey) {
    this.controllerKey = controllerKey;
  }

  public float[] getControllerData() {
    return controllerData;
  }

  public void setControllerData(float[] controllerData) {
    this.controllerData = controllerData;
  }

  public long getFlags() {
    return flags;
  }

  public void setFlags(long flags) {
    this.flags = flags;
  }

  public MdlNodeType getNodeType() {
    return nodeType;
  }

  public void setNodeType(MdlNodeType nodeType) {
    this.nodeType = nodeType;
  }

  public MdlMeshHeader getMeshHeader() {
    return meshHeader;
  }

  public void setMeshHeader(MdlMeshHeader meshHeader) {
    this.meshHeader = meshHeader;
  }

  public MdlReferenceNode getReferenceNode() {
    return referenceNode;
  }

  public void setReferenceNode(MdlReferenceNode referenceNode) {
    this.referenceNode = referenceNode;
  }

  public Quaternion[] getOrientation() {
    return this.orientation;
  }

  public Vector[] getPosition() {
    return this.position;
  }

  public float[] getScale() {
    return this.scale;
  }

  public float[] getPositionTimings() {
    return this.positionTimings;
  }

  public float[] getOrientationTimings() {
    return this.orientationTimings;
  }

}