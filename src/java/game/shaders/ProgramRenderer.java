package game.shaders;

import game.Context;
import game.Renderable;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL20;

public class ProgramRenderer implements Renderable {
  
  private static Logger logger = Logger.getLogger(ProgramRenderer.class);

  Context       context;
  Renderable renderable;

  Program       program;
  ShaderMatrix  rot;
  ShaderArray   noise;
  
  public ProgramRenderer(Context context, Renderable renderable, String shader) {
    this.context = context;
    this.renderable = renderable;
    
    program = new Program();
    program.attach(new Shader("shaders/" + shader + ".vert", GL20.GL_VERTEX_SHADER));
    program.attach(new Shader("shaders/" + shader + ".frag", GL20.GL_FRAGMENT_SHADER));
    program.link();
  }
  
  public ProgramRenderer withShaderVariable(ShaderVariable shaderMatrix) {
    program.use();
    shaderMatrix.use(program);
    program.unuse();
    return this;
  }
  
  public void render() {
    program.use();
    renderable.render();
    program.unuse();
  }
  
  public void register() {
    context.getScene().register(this);
  }

}

