package game.nwn.readers;

import game.math.Vector;

public class MdlMeshHeader {
  long[]       meshRoutines;      
  MdlFace[]    faces;
  Vector       bMin;
  Vector       bMax;
  float       radius;
  Vector       bAverage;
  Vector       diffuse;
  Vector       ambient;
  Vector       specular;
  float       shininess;
  long        shadow;
  long        beaming;
  long        render;
  long        transparencyHint;
  long        unknown5;
  String[]    textures;
  long        tileFade;
  long[]      vertexIndices;
  long[]      leftOverFaces;
  long        vertexIndicesCount;
  long[]      rawVertexIndices;
  long        something3Offset;
  long        something3Count;
  int         triangleMode;
  // pad 3 + 4
  Vector[]     vertices;
  long         vertexCount;
  long         textureCount;
  Vector[][]   tverts;
  Vector[]     normals;
  Vector[]     colors;
  Vector[][]   bumpMaps;
  float[]     floatMap;
  int          rotateTexture;
  long        lightMapped;
  float       faceNormalSumDiv2;

  public long[] getMeshRoutines() {
    return meshRoutines;
  }
  
  public void setMeshRoutines(long[] meshRoutines) {
    this.meshRoutines = meshRoutines;
  }
  
  public MdlFace[] getFaces() {
    return faces;
  }
  
  public void setFaces(MdlFace[] faces) {
    this.faces = faces;
  }
  public Vector getbMin() {
    return bMin;
  }
  public void setbMin(Vector bMin) {
    this.bMin = bMin;
  }
  public Vector getbMax() {
    return bMax;
  }
  public void setbMax(Vector bMax) {
    this.bMax = bMax;
  }
  public float getRadius() {
    return radius;
  }
  public void setRadius(float radius) {
    this.radius = radius;
  }
  public Vector getbAverage() {
    return bAverage;
  }
  public void setbAverage(Vector bAverage) {
    this.bAverage = bAverage;
  }
  public Vector getDiffuse() {
    return diffuse;
  }
  public void setDiffuse(Vector diffuse) {
    this.diffuse = diffuse;
  }
  public Vector getAmbient() {
    return ambient;
  }
  public void setAmbient(Vector ambient) {
    this.ambient = ambient;
  }
  public Vector getSpecular() {
    return specular;
  }
  public void setSpecular(Vector specular) {
    this.specular = specular;
  }
  public float getShininess() {
    return shininess;
  }
  public void setShininess(float shininess) {
    this.shininess = shininess;
  }
  public long getShadow() {
    return shadow;
  }
  public void setShadow(long shadow) {
    this.shadow = shadow;
  }
  public long getBeaming() {
    return beaming;
  }
  public void setBeaming(long beaming) {
    this.beaming = beaming;
  }
  public long getRender() {
    return render;
  }
  public void setRender(long render) {
    this.render = render;
  }
  public long getTransparencyHint() {
    return transparencyHint;
  }
  public void setTransparencyHint(long transparencyHint) {
    this.transparencyHint = transparencyHint;
  }
  public long getUnknown5() {
    return unknown5;
  }
  public void setUnknown5(long unknown5) {
    this.unknown5 = unknown5;
  }
  public String[] getTextures() {
    return textures;
  }
  public void setTextures(String[] textures) {
    this.textures = textures;
  }
  public long getTileFade() {
    return tileFade;
  }
  public void setTileFade(long tileFade) {
    this.tileFade = tileFade;
  }
  public long[] getVertexIndices() {
    return vertexIndices;
  }
  public void setVertexIndices(long[] vertexIndices) {
    this.vertexIndices = vertexIndices;
  }
  public long[] getLeftOverFaces() {
    return leftOverFaces;
  }
  public void setLeftOverFaces(long[] leftOverFaces) {
    this.leftOverFaces = leftOverFaces;
  }
  public long getVertexIndicesCount() {
    return vertexIndicesCount;
  }
  public void setVertexIndicesCount(long vertexIndicesCount) {
    this.vertexIndicesCount = vertexIndicesCount;
  }
  public long[] getRawVertexIndices() {
    return rawVertexIndices;
  }
  public void setRawVertexIndices(long[] rawVertexIndices) {
    this.rawVertexIndices = rawVertexIndices;
  }
  public long getSomething3Offset() {
    return something3Offset;
  }
  public void setSomething3Offset(long something3Offset) {
    this.something3Offset = something3Offset;
  }
  public long getSomething3Count() {
    return something3Count;
  }
  public void setSomething3Count(long something3Count) {
    this.something3Count = something3Count;
  }
  public int getTriangleMode() {
    return triangleMode;
  }
  public void setTriangleMode(int triangleMode) {
    this.triangleMode = triangleMode;
  }
  public Vector[] getVertices() {
    return vertices;
  }
  public void setVertices(Vector[] vertices) {
    this.vertices = vertices;
  }
  public long getVertexCount() {
    return vertexCount;
  }
  public void setVertexCount(long vertexCount) {
    this.vertexCount = vertexCount;
  }
  public long getTextureCount() {
    return textureCount;
  }
  public void setTextureCount(long textureCount) {
    this.textureCount = textureCount;
  }

  public Vector[][] getTexturePoints() {
    return tverts;
  }

  public Vector[] getColors() {
    return colors;
  }

}