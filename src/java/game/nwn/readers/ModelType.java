package game.nwn.readers;

public enum ModelType {
  
  EFFECT(0x1),
  TYPE(0x2),
  CHARACTER(0x4),
  DOOR(0x8),
  UNKNOWN(-1);
  
  private int id;

  ModelType(int id) {
    this.id = id;
  }
  
  static public ModelType withId(int id) {
    for(ModelType type: values()) {
      if ( id == type.id ) {
        return type;
      }
    }
    return UNKNOWN;
  }

}
