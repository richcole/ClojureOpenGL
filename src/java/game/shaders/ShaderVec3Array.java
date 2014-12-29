package game.shaders;


import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;

public class ShaderVec3Array implements ShaderVariable {
  String name;
  FloatBuffer buf;

  public ShaderVec3Array(String name, FloatBuffer buf) {
    this.name = name;
    this.buf = buf;
  }
  
  public void use(Program program) {
    GL20.glUniform3(GL20.glGetUniformLocation(program.getId(), name), buf);
  }
}