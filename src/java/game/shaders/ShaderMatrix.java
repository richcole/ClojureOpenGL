package game.shaders;

import game.math.Matrix;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class ShaderMatrix implements ShaderVariable {
  String name;
  FloatBuffer buf = BufferUtils.createFloatBuffer(16);

  public ShaderMatrix(String name, Matrix m) {
    this.name = name;
    m.writeToBuffer(buf);
  }
  
  public void use(Program program) {
    GL20.glUniformMatrix4(GL20.glGetUniformLocation(program.getId(), name), true, buf);
  }
}