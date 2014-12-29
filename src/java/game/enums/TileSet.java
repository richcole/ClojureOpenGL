package game.enums;


public enum TileSet implements Res {
  
  Tin01("tin01");

  String resName;
  
  TileSet(String resName) {
    this.resName = resName;
  }
  
  public String getResName() {
    return resName;
  }
  
  public String getResType() {
    return "set";
  }
  
}
