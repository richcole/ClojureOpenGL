package game.shaders;


import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;

public class ShaderArray implements ShaderVariable {
  String name;
  FloatBuffer buf;

  public ShaderArray(String name, FloatBuffer buf) {
    this.name = name;
    this.buf = buf;
  }
  
  public void use(Program program) {
    GL20.glUniform1(GL20.glGetUniformLocation(program.getId(), name), buf);
  }
  
  public FloatBuffer getBuffer() {
    return buf;
  }
}