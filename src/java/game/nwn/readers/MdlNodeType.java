package game.nwn.readers;

import java.util.Map;

import com.google.common.collect.Maps;

public class MdlNodeType {
  
  static final int HAS_DUMMY   = 0x001;
  static final int HAS_LIGHT   = 0x002;
  static final int HAS_EMITTER = 0x004;
  static final int HAS_CAMERA  = 0x008;
  static final int HAS_REF     = 0x010;
  static final int HAS_MESH    = 0x020;
  static final int HAS_SKIN    = 0x040;
  static final int HAS_ANIM    = 0x080;
  static final int HAS_DANGLY  = 0x100;
  static final int HAS_AABB    = 0x200;
  static final int HAS_UNKNONW = 0x400;

  private long id;
  
  private static Map<Long, MdlNodeType> types = Maps.newHashMap();

  MdlNodeType(long id) {
    this.id = id;
  }
  
  public static MdlNodeType getMdlNodeType(long flags) {
    return new MdlNodeType(flags);
  }

  public boolean hasLight() {
    return (id & HAS_LIGHT) != 0;
  }

  public boolean hasEmitter() {
    return (id & HAS_EMITTER) != 0;
  }

  public boolean hasMesh() {
    return (id & HAS_MESH) != 0;
  }

  public boolean hasSkin() {
    return (id & HAS_SKIN) != 0;
  }

  public boolean hasAnim() {
    return (id & HAS_ANIM) != 0;
  }
  
  public boolean hasCamera() {
    return (id & HAS_CAMERA) != 0;
  }

  public boolean hasDangly() {
    return (id & HAS_CAMERA) != 0;
  }

  public boolean hasAABB() {
    return (id & HAS_AABB) != 0;
  }

  public boolean hasRef() {
    return (id & HAS_REF) != 0;
  }
}
