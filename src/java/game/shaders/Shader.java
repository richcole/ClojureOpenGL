package game.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shader {
  int id;
  
  Shader(String filename, int type) {
    id = GL20.glCreateShader(type);
    GL20.glShaderSource(id, Util.readFileAsString(filename));
    GL20.glCompileShader(id);
    if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
      throw new RuntimeException("Unable to compile shader: " + GL20.glGetShaderInfoLog(id, 4*1024));
    }
  }
  
  int getId() {
    return id;
  }
}