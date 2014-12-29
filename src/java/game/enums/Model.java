package game.enums;

public enum Model implements Res {
  
  Wererat("c_wererat"),
  Tcn01_r10_01("tcn01_r10_01");
  
  private String resName;

  Model(String resName) {
    this.resName = resName;
  }
  
  public String getResName() {
    return resName;
  }
  
  public String getResType() {
    return "mdl";
  }
  
}
