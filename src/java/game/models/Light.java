package game.models;

import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_EMISSION;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_QUADRATIC_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_SPECULAR;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLightf;
import game.Context;
import game.Renderable;
import game.math.Vector;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;


public class Light implements Renderable {
  
  private int         lightNumber;
  private Vector      position;
  private FloatBuffer lightPosition;
  private FloatBuffer whiteLight;
  private FloatBuffer lModelAmbient;

  public Light(Context context, Vector position, int lightNumber) {
    this.lightNumber = lightNumber;
    this.position    = position;
    context.getScene().register(this);
    
    lightPosition = BufferUtils.createFloatBuffer(4);
    lightPosition.put(-1.0f).put(-1.0f).put(-1.0f).put(1.0f).flip();
    
    whiteLight = BufferUtils.createFloatBuffer(4);
    whiteLight.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
    
    lModelAmbient = BufferUtils.createFloatBuffer(4);
    lModelAmbient.put(0.5f).put(0.5f).put(0.5f).put(1.0f).flip();
  }
  
  @Override
  public void render() {
    lightPosition.clear();
    lightPosition.put((float)position.x());
    lightPosition.put((float)position.y());
    lightPosition.put((float)position.z());
    lightPosition.put((float)1.0f);
    lightPosition.flip();
            
    int glLightIndex = GL_LIGHT0 + lightNumber;
    glEnable(glLightIndex);
    glLight(glLightIndex, GL_POSITION,      lightPosition);  
    glLight(glLightIndex, GL_SPECULAR,      whiteLight);     
    glLight(glLightIndex, GL_DIFFUSE,       whiteLight);     
    glLight(glLightIndex, GL_EMISSION,      whiteLight);
    glLightf(glLightIndex, GL_QUADRATIC_ATTENUATION, 1.0f/10000);
  }

  public void move(Vector velocity) {
    position = position.plus(velocity);
  }

}
