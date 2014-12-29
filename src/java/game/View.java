package game;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.GLU;

public class View {

  float perspAngle;
  float aspect;
  float zNear;
  float zFar;
  
  DisplayMode mode;
  Context context;
  
  View(Context context) {
    this.context = context;
    perspAngle = 45f;
    aspect = 1.0f;
    zNear = 1.0f;
    zFar = 100000.0f;
    mode = Display.getDisplayMode();
  }
  
  public float getPerspAngle() {
    return perspAngle;
  }
  
  public void setPerspAngle(float perspAngle) {
    this.perspAngle = perspAngle;
  }
  
  public float getAspect() {
    return aspect;
  }
  
  public void setAspect(float aspect) {
    this.aspect = aspect;
  }
  
  public float getZNear() {
    return zNear;
  }
  
  public void setZNear(float zNear) {
    this.zNear = zNear;
  }
  
  public float getZFar() {
    return zFar;
  }
  
  public void setZFar(float zFar) {
    this.zFar = zFar;
  }

  public DisplayMode getMode() {
    return mode;
  }

  public void setMode(DisplayMode mode) {
    this.mode = mode;
  }
  
  public float getWidth() {
    return mode.getWidth();
  }

  public float getHeight() {
    return mode.getHeight();
  }

  public void init() {
    mode = Display.getDisplayMode();

    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glEnable(GL12.GL_TEXTURE_3D);    
    GL11.glShadeModel(GL11.GL_SMOOTH);       
    GL11.glEnable (GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glDepthFunc(GL11.GL_LESS);
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glFrontFace(GL11.GL_CCW);
    
    GL11.glEnable(GL11.GL_LIGHTING);
    
    GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);               
    GL11.glClearDepth(1f);

    GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
    GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
    GL11.glViewport(0, 0, (int)getWidth(), (int)getHeight());
    
    GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, context.getColors().getGray9());
  }

  public void perspectiveView() {
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glEnable(GL12.GL_TEXTURE_3D);
    GL11.glEnable (GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glDepthFunc(GL11.GL_LEQUAL);
    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    GLU.gluPerspective(getPerspAngle(), getAspect(), getZNear(), getZFar());
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();
  }
  
  public void orthoView() {
    GL11.glDisable(GL12.GL_TEXTURE_3D);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    GL11.glOrtho(0, getWidth(), getHeight(), 0, 1, -1);
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();
  }

  public void clear() {
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
  }

}
