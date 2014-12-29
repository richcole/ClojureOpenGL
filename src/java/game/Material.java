package game;

import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_SHININESS;
import static org.lwjgl.opengl.GL11.glMaterial;
import static org.lwjgl.opengl.GL11.glMaterialf;

public class Material implements Renderable {
  
  private Context context;

  public Material(Context context) {
    this.context = context;
  }
  
  public void render() {
    // glMaterial(GL_FRONT_AND_BACK,  GL_EMISSION,  context.getColors().getGray());
    glMaterial(GL_FRONT_AND_BACK, GL_AMBIENT, context.getColors().getWhite());          
    glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 50.0f);          
    glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 50.0f);          
  }
}
