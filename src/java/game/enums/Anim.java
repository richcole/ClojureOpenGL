package game.enums;

public enum Anim {
  
  CWALK("cwalk"),
  CPAUSE1("cpause1"),
  CA1STAB("ca1stab"),
  NONE("none");
  
  String name;
  
  Anim(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  

}
