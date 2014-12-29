package game;

import game.math.Vector;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class SelectionRay {
  
  FloatBuffer projection;
  FloatBuffer modelView;
  IntBuffer viewport;
  FloatBuffer nearPos;
  FloatBuffer farPos;
  Context context;
  
  SelectionRay(Context context) {
    this.context = context;
    
    projection = BufferUtils.createFloatBuffer(16);
    modelView = BufferUtils.createFloatBuffer(16);
    viewport = BufferUtils.createIntBuffer(16);
    nearPos = BufferUtils.createFloatBuffer(3);
    farPos = BufferUtils.createFloatBuffer(3);
  }

  public void updateViewMatrix() {
    GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
    GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
    GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
  }
  
  public Vector getSelectionRay(float x, float y) {
    View view = context.getView();
    nearPos.clear();
    farPos.clear();
    GLU.gluUnProject(x, y, 0f, modelView, projection, viewport, nearPos);
    GLU.gluUnProject(x, y, 1.0f, modelView, projection, viewport, farPos);
    float fx = farPos.get(0);
    float fy = farPos.get(1);
    float fz = farPos.get(2);
    float nx = nearPos.get(0);
    float ny = nearPos.get(1);
    float nz = nearPos.get(2);
    return new Vector(fx - nx, fy - ny, fz - nz);    
  }
  
}
