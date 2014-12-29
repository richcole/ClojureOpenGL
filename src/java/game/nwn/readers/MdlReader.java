package game.nwn.readers;

import game.math.Quaternion;
import game.math.Vector;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MdlReader {
  
  private static Logger logger = Logger.getLogger(MdlReader.class);

  Resource resource;
  Header header;
  BinaryFileReader inp;
  Map<Long, MdlNodeHeader> modelNodeHeaders = Maps.newHashMap();
  Map<Long, MdlGeometryHeader> geometryHeaders = Maps.newHashMap();
  Map<Long, MdlModel> mdlModels = Maps.newHashMap();
  KeyReader keyReader;

  public MdlReader(KeyReader keyReader, Resource resource) {
    this.keyReader = keyReader;
    this.resource = resource;
    this.inp = resource.getReader().getInp();
  }
  
  public MdlModel readModel() {
    inp.seek(resource.getOffset());
    this.header = readHeader();
    this.header.setModel(readMdlModel());
    return header.getModel();
  }
  
  private void printModel() {
    printGeom("", header.getModel().getGeometryHeader().getGeometry());
  }

  private void printGeom(String indent, MdlNodeHeader mdlNodeHeader) {
    logger.info(indent + " name: " + mdlNodeHeader.getName());
    logger.info(indent + " flags: " + mdlNodeHeader.getFlags());
    if ( mdlNodeHeader.getMeshHeader() != null ) {
      for(String texture: mdlNodeHeader.getMeshHeader().getTextures()) {
        logger.info(indent + " texture: " + texture);
      }
    }
    for(MdlNodeHeader c: mdlNodeHeader.getChildren()) {
      printGeom(indent + "  ", c);
    }
  }

  public MdlAnimationEvent readMdlAnimationEvent() {
    MdlAnimationEvent r = new MdlAnimationEvent();
    r.after = inp.readFloat();
    r.name = inp.readNullString(32);
    return r;
  }
  
  public MdlAnimation readMdlAnimation() {
    MdlAnimation r = new MdlAnimation();
    r.geometryHeader = readMdlGeometryHeader();
    r.length = inp.readFloat();
    r.transTime = inp.readFloat();
    r.animRoot = inp.readNullString(64);
    r.events  = readMdlAnimationEventList();
    return r;
  }

  MdlAnimationEvent[] readMdlAnimationEventList() {
    ArrayRef arrayRef = readArrayRef();
    long mark = inp.pos();
    MdlAnimationEvent[] r = new MdlAnimationEvent[arrayRef.count];
    for(int i=0;i<arrayRef.count;++i) {
      seekOffset(arrayRef.offset + i*MdlAnimationEvent.SIZE);
      r[i] = readMdlAnimationEvent();
    }
    inp.seek(mark);
    return r;
  }

  List<MdlAnimation> readIndirectMdlAnimationList() {
    ArrayRef arrayRef = readArrayRef();
    long mark = inp.pos();
    List<MdlAnimation> r = Lists.newArrayList();
    for(int i=0;i<arrayRef.count;++i) {
      seekOffset(arrayRef.offset + i*BinaryFileReader.WORD_SIZE);
      seekOffset(inp.readWord());
      r.add(readMdlAnimation());
    }
    inp.seek(mark);
    return r;
  }

  public ArrayRef readArrayRef() {
    ArrayRef r = new ArrayRef();
    r.offset = inp.readWord();
    r.count = (int) inp.readWord();
    r.alloc = inp.readWord();
    return r;
  }
  
  MdlControllerKey[] readControllerKeyList() {
    ArrayRef arrayRef = readArrayRef();
    long mark = inp.pos();
    MdlControllerKey[] r = new MdlControllerKey[arrayRef.count];
    for(int i=0;i<arrayRef.count;++i) {
      seekOffset(arrayRef.offset + i*MdlControllerKey.SIZE);
      r[i] = readControllerKey();
    }
    inp.seek(mark);
    return r;
  }
  
  MdlControllerKey readControllerKey() {
    MdlControllerKey r = new MdlControllerKey();
    r.type = inp.readWord();
    r.rows = inp.readShort();
    r.keyOffset = inp.readShort();
    r.dataOffset = inp.readShort();
    r.columns = (int) inp.readByte();
    r.pad = (int) inp.readByte();
    return r;
  }
  
  float[] readFloatList() {
    ArrayRef arrayRef = readArrayRef();
    long mark = inp.pos();
    float[] r = new float[(int)arrayRef.count];
    for(int i=0;i<arrayRef.count;++i) {
      seekOffset(arrayRef.offset + i*4);
      r[i] = inp.readFloat();
    }
    inp.seek(mark);
    return r;
  }
  
  Vector[] readVectorList() {
    ArrayRef arrayRef = readArrayRef();
    long mark = inp.pos();
    Vector[] r = new Vector[(int)arrayRef.count];
    for(int i=0;i<arrayRef.count;++i) {
      seekOffset(arrayRef.offset + i*12);
      float x = inp.readFloat();
      float y = inp.readFloat();
      float z = inp.readFloat();
      r[i] = new Vector(x, y, z, 1.0);
    }
    inp.seek(mark);
    return r;
  }
  
  MdlFace[] readFaceList() {
    ArrayRef arrayRef = readArrayRef();
    long mark = inp.pos();
    MdlFace[] r = new MdlFace[(int)arrayRef.count];
    for(int i=0;i<arrayRef.count;++i) {
      seekOffset(arrayRef.offset + i*MdlFace.SIZE);
      r[i] = readFace();
    }
    inp.seek(mark);
    return r;
  }
  
  MdlFace readFace() {
    MdlFace r = new MdlFace();
    r.planeNormal = readVector();
    r.planeDistance = inp.readFloat();
    r.surface = inp.readWord();
    r.adjFace = inp.readSignedShorts(3);
    r.vertex  = inp.readShorts(3);
    return r;
  }
  
  Vector readVector() {
    float x = inp.readFloat();
    float y = inp.readFloat();
    float z = inp.readFloat();
    return new Vector(x, y, z, 1.0);
  }
  
  Vector read2Vector() {
    float x = inp.readFloat();
    float y = inp.readFloat();
    return new Vector(x, y, 0.0, 1.0);
  }

  Vector[] readVectors(int len) {
    Vector r[] = new Vector[len];
    for(int i=0;i<len;++i) {
      r[i] = readVector();
    }
    return r;
  }
  
  Vector[] readIndirectVectorList() {
    ArrayRef arrayRef = readArrayRef();
    long mark = inp.pos();
    Vector[] r = new Vector[arrayRef.count];
    for(int i=0;i<arrayRef.count;++i) {
      seekOffset(arrayRef.offset + i*BinaryFileReader.VECTOR_SIZE);
      long p = inp.readWord();
      if ( p != 0xFFFFFFFFL) {
        seekOffset(p);
        r[i] = readVector();
      }
    }
    inp.seek(mark);
    return r;
  }
  
  Vector[] readIndirectVectors(int len) {
    Vector[] r = new Vector[len];
    for(int i=0;i<len;++i) {
      long p = inp.readWord();
      if ( p != 0xFFFFFFFFL) {
        long mark = seekOffset(p);
        r[i] = readVector();
        inp.seek(mark);
      }
    }
    return r;
  }
  
  Vector[] readIndirect2Vectors(int len) {
    Vector[] r = new Vector[len];
    for(int i=0;i<len;++i) {
      r[i] = readIndirect2Vector();
    }
    return r;
  }
  
  Vector readIndirect2Vector() {
    long p = inp.readWord();
    long mark = seekOffset(p);
    float x = inp.readFloat();
    float y = inp.readFloat();
    Vector r = new Vector(x, y, 0, 1.0);
    inp.seek(mark);
    return r;
  }

  Vector readIndirectVector() {
    long p = inp.readWord();
    long mark = seekOffset(p);
    Vector r = readVector();
    inp.seek(mark);
    return r;
  }

  long readIndirectWord() {
    long p = inp.readWord();
    long mark = seekOffset(p);
    long r = inp.readWord();
    inp.seek(mark);
    return r;
  }

  float readIndirectFloat() {
    long p = inp.readWord();
    long mark = seekOffset(p);
    float r = inp.readFloat();
    inp.seek(mark);
    return r;
  }

  MdlNodeHeader[] readIndirectModelNodeHeaderList() {
    ArrayRef arrayRef = readArrayRef();
    long mark = inp.pos();
    MdlNodeHeader[] r = new MdlNodeHeader[arrayRef.count];
    for(int i=0;i<arrayRef.count;++i) {
      seekOffset(arrayRef.offset + i*BinaryFileReader.WORD_SIZE);
      r[i] = readModelNodeHeader(inp.readWord());
    }
    inp.seek(mark);
    return r;
  }
  
  long[] readWordList() {
    ArrayRef arrayRef = readArrayRef();
    long mark = inp.pos();
    long[] r = new long[arrayRef.count];
    for(int i=0;i<arrayRef.count;++i) {
      seekOffset(arrayRef.offset + i*4);
      r[i] = inp.readWord();
    }
    inp.seek(mark);
    return r;
  }

  long[] readIndirectWordList() {
    ArrayRef arrayRef = readArrayRef();
    long mark = inp.pos();
    long[] r = new long[arrayRef.count];
    for(int i=0;i<arrayRef.count;++i) {
      seekOffset(arrayRef.offset + i*4);
      seekOffset(inp.readWord());
      r[i] = inp.readWord();
    }
    inp.seek(mark);
    return r;
  }

  long[] readIndirectShortList() {
    ArrayRef arrayRef = readArrayRef();
    long mark = inp.pos();
    long[] r = new long[arrayRef.count];
    for(int i=0;i<arrayRef.count;++i) {
      seekOffset(arrayRef.offset + i*4);
      seekOffset(inp.readWord());
      r[i] = inp.readShort();
    }
    inp.seek(mark);
    return r;
  }

  public Header readHeader() {
    inp.seek(resource.getOffset());
    Header r = new Header();
    r.zero = inp.readWord();
    if ( r.zero != 0 ) {
      return r;
    }
    r.dataOffset = inp.readWord();
    r.dataSize = inp.readWord();
    return r;
  }
  
  long seekOffset(long offset) {
    long mark = inp.pos();
    if ( offset >= resource.getLength() ) {
      throw new RuntimeException("Overrun offset=" + offset);
    }
    long seekPos = offset + resource.getOffset() + 12;
    inp.seek(seekPos);
    return mark;
  }
  
  long seekExternalOffset(long offset) {
    long mark = inp.pos();
    long seekPos = offset + resource.getOffset() + 12;
    inp.seek(seekPos);
    return mark;
  }

  public MdlNodeHeader readModelNodeHeader(Long offset) {
    if ( offset == 0 ) {
      return null;
    }
    MdlNodeHeader r = modelNodeHeaders.get(offset);
    if ( r == null ) {
      long mark = seekOffset(offset);
      modelNodeHeaders.put(offset, r);
      r = readMdlNodeHeader();
      inp.seek(mark);
    }
    return r;
  }

  private MdlNodeHeader readMdlNodeHeader() {
    MdlNodeHeader r;
    r = new MdlNodeHeader();
    r.nodeRoutines = inp.readWords(6);
    r.inheritColor = inp.readWord();
    r.partNumber = inp.readWord();
    r.name = inp.readNullString(32);
    r.geomemtryHeader = readGeomHeader(inp.readWord());
    r.parentNode = readModelNodeHeader(inp.readWord());
    r.children = readIndirectModelNodeHeaderList();
    r.controllerKey = readControllerKeyList();
    r.controllerData = readFloatList();
    r.flags = inp.readWord();
    r.nodeType = MdlNodeType.getMdlNodeType(r.flags);
    
    for(MdlControllerKey key: r.controllerKey) {
      if ( key.type == MdlControllerKey.KEY_POSITION ) {
        r.position = getControllerData3Vector(key.dataOffset, r.controllerData, key.columns, key.rows);
        r.positionTimings = getControllerFloat(key.dataOffset - key.rows, r.controllerData, key.rows);
      }
      if ( key.type == MdlControllerKey.KEY_ORIENTATION) {
        r.orientation = getControllerQuaternion(key.dataOffset, r.controllerData, key.columns, key.rows);
        r.orientationTimings = getControllerFloat(key.dataOffset - key.rows, r.controllerData, key.rows);
      }
      if ( key.type == MdlControllerKey.KEY_SCALE ) {
        r.scale = getControllerFloat(key.dataOffset, r.controllerData, key.rows);
      }
    }
    if ( r.nodeType.hasLight() ) {
      inp.readBytes(0x5C);
    }
    if ( r.nodeType.hasEmitter() ) {
      inp.readBytes(0xD8);
    }
    if ( r.nodeType.hasRef() ) {
      r.referenceNode = readMdlReferenceNode();
    }
    if ( r.nodeType.hasMesh() ) {
      r.meshHeader = readMdlMeshHeader();
    }
    if ( r.nodeType.hasSkin() ) {
      inp.readBytes(0x64);
    }
    if ( r.nodeType.hasAnim() ) {
      r.animMeshNode = readMdlAnimMeshNode();
    }
    if ( r.nodeType.hasDangly() ) {
      inp.readBytes(0x18);
    }
    if ( r.nodeType.hasAABB() ) {
      inp.readBytes(0x4);
    }
    return r;
  }
  
  private MdlAnimMeshNode readMdlAnimMeshNode() {
    MdlAnimMeshNode r = new MdlAnimMeshNode();
    r.samplePeriod = inp.readFloat();
    r.animVerts = readVectorList();
    r.animTVerts = readVectorList();
    r.animNormals = readVectorList();
    long vertexSets = inp.readWord();
    long textureSets = inp.readWord();
    r.vertexSetCount = inp.readWord();
    r.textureSetCount = inp.readWord();
    r.vertexSets = readVectorListAt(vertexSets, header.dataOffset, (int) r.vertexSetCount);
    r.textureSets = readVectorListAt(textureSets, header.dataOffset, (int) r.textureSetCount);
    return r;
  }

  private Vector[] getControllerData3Vector(int offset, float[] data, int columns, int rows) {
    if ( columns != 3 ) {
      throw new RuntimeException("Expected 3 columns");
    }
    Vector[] r = new Vector[rows];
    for(int i=0;i<rows; ++i) {
      int index = offset + (i*3);
      r[i] = new Vector(data[index + 0], data[index+1], data[index+2], 1.0);
    }
    return r;
  }

  private Quaternion[] getControllerQuaternion(int offset, float[] data, int columns, int rows) {
    if ( columns != 4 ) {
      throw new RuntimeException("Expected 4 columns");
    }
    Quaternion[] r = new Quaternion[rows];
    for(int i=0;i<rows; ++i) {
      int index = offset + (i*4);
      r[i] = new Quaternion(data[index+0], data[index+1], data[index+2], data[index+3]);
    }
    return r;
  }

  private float[] getControllerFloat(int offset, float[] data, int rows) {
    float[] r = new float[rows];
    for(int i=0;i<rows; ++i) {
      r[i] = data[offset + i];
    }
    return r;
  }
  
  private MdlReferenceNode readMdlReferenceNode() {
    MdlReferenceNode r = new MdlReferenceNode();
    r.refModel = inp.readNullString(64);
    r.reattachable = inp.readWord();
    return r;
  }
  
  private MdlMeshHeader readMdlMeshHeader() {
    MdlMeshHeader r = new MdlMeshHeader();
    r.meshRoutines = inp.readWords(2);
    r.faces = readFaceList();
    r.bMin = readVector();
    r.bMax = readVector();
    r.radius = inp.readFloat();
    r.bAverage = readVector();
    r.diffuse = readVector();
    r.ambient = readVector();
    r.specular = readVector();
    r.shininess = inp.readFloat();
    r.shadow = inp.readWord();
    r.beaming = inp.readWord();
    r.render = inp.readWord();
    r.transparencyHint = inp.readWord();
    r.unknown5 = inp.readWord();
    r.textures = inp.readNullStrings(64, 4);
    r.tileFade = inp.readWord();
    r.vertexIndices = readIndirectWordList();
    r.leftOverFaces = readWordList();
    r.vertexIndices = readWordList();
    r.rawVertexIndices = readIndirectShortList();
    r.something3Offset = inp.readWord();
    r.something3Count = inp.readWord();
    r.triangleMode = (int) inp.readByte();
    inp.readBytes(3);
    long pad = inp.readWord();
    long vertPointer = inp.readWord();
    r.vertexCount = inp.readShort();
    r.vertices = readVectorListAt(vertPointer, header.dataOffset, (int) r.vertexCount);
    r.textureCount = inp.readShort();
    r.tverts = new Vector[4][];
    for(int i=0;i<4;++i) {
      r.tverts[i] = read2VectorListAt(inp.readWord(), header.dataOffset, (int) r.vertexCount);
    }
    r.normals = readVectorListAt(inp.readWord(), header.dataOffset, (int) r.vertexCount);
    long[] colors = readWordListAt(inp.readWord(), header.dataOffset, (int) r.vertexCount);
    r.colors = new Vector[(int)r.vertexCount];
    for(int i=0;i<r.vertexCount;++i) {
      r.colors[i] = new Vector(((colors[i] >> 0) & 0xff) / 255.0, ((colors[i] >> 8) & 0xff) / 255.0, ((colors[i] >> 16) & 0xff) / 255.0, 1.0f);
    }
    r.bumpMaps = new Vector[5][];
    for(int i=0;i<5;++i) {
      r.bumpMaps[i] = readVectorListAt(inp.readWord(), header.dataOffset, (int) r.vertexCount);
    }
    r.floatMap = readFloatListAt(inp.readWord(), header.dataOffset, (int) r.vertexCount);
    r.lightMapped = inp.readByte();
    r.rotateTexture = (int) inp.readByte();
    long pad2 = inp.readShort();
    r.faceNormalSumDiv2 = inp.readFloat();
    return r;
  }
  
  public Vector[] readVectorListAt(long offset, long dataOffset, int len) {
    Vector[] r = new Vector[len];
    if ( offset == 0xFFFFFFFFL ) {
      return r;
    }
    long mark = seekOffset(offset + dataOffset);
    for(int i=0;i<len;++i) {
      r[i] = readVector();
    }
    inp.seek(mark);
    return r;
  }

  public long[] readWordListAt(long offset, long dataOffset, int len) {
    long[] r = new long[len];
    if ( offset == 0xFFFFFFFFL ) {
      return r;
    }
    long mark = seekOffset(offset + dataOffset);
    for(int i=0;i<len;++i) {
      r[i] = inp.readWord();
    }
    inp.seek(mark);
    return r;
  }

  public float[] readFloatListAt(long offset, long dataOffset, int len) {
    float[] r = new float[len];
    if ( offset == 0xFFFFFFFFL ) {
      return r;
    }
    long mark = seekOffset(offset + dataOffset);
    for(int i=0;i<len;++i) {
      r[i] = inp.readFloat();
    }
    inp.seek(mark);
    return r;
  }

  public Vector[] read2VectorListAt(long offset, long dataOffset, int len) {
    Vector[] r = new Vector[len];
    if ( offset == 0xFFFFFFFFL ) {
      return r;
    }
    long mark = seekOffset(offset + dataOffset);
    for(int i=0;i<len;++i) {
      r[i] = read2Vector();
    }
    inp.seek(mark);
    return r;
  }

  public MdlGeometryHeader readGeomHeader(long offset) {
    if ( offset == 0 ) {
      return null;
    }
    long mark = seekOffset(offset);
    MdlGeometryHeader r = geometryHeaders.get(offset);
    if ( r == null ) { 
      r = readMdlGeometryHeader();
      geometryHeaders.put(offset, r);
    }
    inp.seek(mark);
    return r;
  }
  
  public MdlGeometryHeader readMdlGeometryHeader() {
    MdlGeometryHeader r = new MdlGeometryHeader();
    r.aulGeomRoutines = inp.readWords(2);
    if ( isAnUnknownModelFormat(r) ) {
      throw new RuntimeException("Unknown model format");
    }
    r.name   = inp.readNullString(64);
    r.geometry = readModelNodeHeader(inp.readWord());
    r.nodeCount = inp.readWord();
    r.rtArray1  = readWordList();
    r.rtArray2  = readWordList();
    r.u2        = inp.readWord();
    r.geomType  = inp.readWord();
    return r;
  }
  
  public MdlModel readMdlModel() {
    MdlModel r = new MdlModel();
    r.setGeometryHeader(readMdlGeometryHeader());
    r.aucFlags = inp.readShort();
    r.classification = inp.readByte();
    r.fog = (int) inp.readByte();
    r.refCount = inp.readWord();
    r.animations = readIndirectMdlAnimationList();
    long superModelPtr = inp.readWord();
    r.bb = inp.readWords(6);
    r.radius = inp.readFloat();
    r.animScale = inp.readFloat();
    r.superModelName = inp.readNullString(64);

    r.animMap = Maps.newHashMap();
    for(MdlAnimation anim: r.animations) {
      r.animMap.put(anim.getGeometryHeader().getName(), anim);
    }

    if ( ! r.superModelName.equalsIgnoreCase("null") ) {
      logger.info("Reading " + r.superModelName);
      long mark = inp.pos();
      r.superModel = keyReader.getModel(r.superModelName);
      inp.seek(mark);
    }
    
    mix(r, r.superModel);

    return r;
  }

  private void mix(MdlModel m, MdlModel parent) {
    if ( parent != null ) {
      if ( parent.animations != null ) {
        m.animations.addAll(parent.animations);
        for(MdlAnimation a: parent.animations) {
          String animName = a.getGeometryHeader().getName();
          if ( m.animMap.get(animName) == null ) {
            logger.info("Adding anim " + animName);
            m.animMap.put(animName, a);
          }
        }
      }
    }
  }

  private boolean isAnUnknownModelFormat(MdlGeometryHeader h) {
    return h.aulGeomRoutines[0] < 1000;
  }

  public Header getHeader() {
    return header;
  }
  
}
