package game.shaders;

import java.io.File;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Files;

public class Util {
  public static String readFileAsString(String filename) {
    File file = new File("../src/main/" + filename);
    if ( ! file.exists() ) {
      file = new File(filename);
    }
    try {
      return Files.toString(file, Charsets.UTF_8);
    } catch(Exception e) {
      Throwables.propagate(e);
      return null;
    }
  }
}