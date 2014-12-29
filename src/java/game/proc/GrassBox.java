package game.proc;

import game.Context;
import game.Renderable;
import game.math.Vector;
import game.shaders.ProgramRenderer;
import game.shaders.ShaderVec3Array;
import game.shaders.ShaderVec4Array;
import game.utils.GLUtils;

import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;

public class GrassBox implements Renderable {
  VertexCloud rect;
  ProgramRenderer box;
  Context context;
  FloatBuffer randBuf;
  ShaderVec4Array eye;
  
  public GrassBox(Context context) {
    int len = 1;
    int randBufLen = len;
    Random random = new Random();
    randBuf = BufferUtils.createFloatBuffer(randBufLen*3);
    for(int i=0;i<randBufLen;++i) {
      randBuf.put((float)(random.nextDouble()-0.5)*10);
      randBuf.put((float)(random.nextDouble()-0.5));
      randBuf.put((float)(random.nextDouble()));
    }
    randBuf.flip();
    
    this.context = context;
    this.rect = new VertexCloud();
    double scale = 100;
    Vector n = Vector.NORMAL.times(-1);
    Vector l = Vector.LEFT;
    Vector z = Vector.Z;
    Vector u = Vector.UP.times(scale/2);
    Vector r = Vector.LEFT.times(-scale/2);
    Vector f = Vector.NORMAL.times(scale/2);
    for(int x=0;x<len;++x) {
      for(int y=0;y<len;++y) {
        Vector p = r.times(x).plus(f.times(y));
        this.rect.addVertex(p.plus(z),         n, new Vector(0, 0, 0, 1), p, r, u);
        this.rect.addVertex(p.plus(u),         n, new Vector(0, 1, 0, 1), p, r, u);
        this.rect.addVertex(p.plus(r),         n, new Vector(1, 0, 0, 1), p, r, u);
        this.rect.addVertex(p.plus(r),         n, new Vector(1, 0, 0, 1), p, r, u);
        this.rect.addVertex(p.plus(r.plus(u)), n, new Vector(1, 1, 0, 1), p, r, u);
        this.rect.addVertex(p.plus(u),         n, new Vector(0, 1, 0, 1), p, r, u);

        this.rect.addVertex(p.plus(z),         l, new Vector(0, 0, 0, 1), p, u, f);
        this.rect.addVertex(p.plus(u),         l, new Vector(1, 0, 0, 1), p, u, f);
        this.rect.addVertex(p.plus(f),         l, new Vector(0, 1, 0, 1), p, u, f);
        this.rect.addVertex(p.plus(f),         l, new Vector(0, 1, 0, 1), p, u, f);
        this.rect.addVertex(p.plus(u),         l, new Vector(1, 0, 0, 1), p, u, f);
        this.rect.addVertex(p.plus(u).plus(f), l, new Vector(1, 1, 0, 1), p, u, f);

      }
    }
    this.rect.freeze();
    this.box = new ProgramRenderer(context, rect, "grass");
    this.box.withShaderVariable(new ShaderVec3Array("a", randBuf));
    eye = new ShaderVec4Array("eye", GLUtils.toFloatBuffer(context.getPlayer().getPos()));
    
    this.box.withShaderVariable(eye);
  }

  @Override
  public void render() {
    GLUtils.writeToFloatBuffer(context.getPlayer().getPos(), eye.getBuffer());
    box.withShaderVariable(eye);
    box.render();
 }

  public void register() {
    context.getScene().register(this);
  }

}
