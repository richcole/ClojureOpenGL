package game.models;

import game.enums.Res;

import java.io.File;

public class ResFiles {
  
  File root;
  
  public ResFiles() {
    root = new File(System.getProperty("user.dir"));
  }

  public File getResFile(String resName, String resType) {
    return new File(root, "res/" + resName + "." + resType.toLowerCase() + ".gz");
  }

  public File getResFile(Res res) {
    return getResFile(res.getResName(), res.getResType());
  }

  public File getImageRes(String name) {
    return new File(root, "res/" + name);
  }

  public void setRootDirectory(File root) {
    this.root = root;
  }

}
