package game.shaders;


import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Program {
  int id;
  
  public Program() {
    id = GL20.glCreateProgram();
  }
  
  public int getId() {
    return id;
  }

  public void attach(Shader shader) {
    GL20.glAttachShader(id, shader.getId());
  }
  
  public void link() {
    GL20.glLinkProgram(id);
    if ( GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE ) {
      throw new RuntimeException("Unable to link program" + ":" + GL20.glGetProgramInfoLog(id, 4*1024));
    }
  }

  public void use() {
    GL20.glUseProgram(id);
  }
  
  public void unuse() {
    GL20.glUseProgram(0);
  }

}