package game;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import javax.vecmath.Vector3d;

import com.google.common.collect.Lists;

public class LogPanel {

  MutableTexture texture;
  int width, height, lineSpacing, leftMargin, boxHeight;
  double renderSpeed = 0;
  double sleepTime;
  
  List<String> texts;

  private Context context;

  public LogPanel(Context context) {
    this.context = context;
    leftMargin = 10;
    lineSpacing = 15;
    boxHeight = 50;
    width = (int) context.getView().getWidth();
    height = (int) context.getView().getHeight();
    texture = new MutableTexture(width, height);
  }

  public String format(Vector3d v) {
    return String.format("(%2.2f,%2.2f,%2.2f)", v.x, v.y, v.z);
  }
  
  public void reset() {
    texts = Lists.newArrayList();
  }
  
  public void writeLine(String format, Object ... args) {
    texts.add(String.format(format, args));
  }

  public void render() {
    reset();
    writeLine("p=%s rate=%3.2f sleep=%3.2f", context.getPlayer().pos, 1000.0 / renderSpeed,  sleepTime);
    renderFlat();
    renderGL();
  }

  private void renderGL() {
    context.getView().orthoView();
    texture.update();
    texture.bind();

    glColor3f(1.0f, 1.0f, 1.0f);
    glBegin(GL_QUADS);  
    glTexCoord2f(0, 0);
    glVertex3f(0, 0, 0);

    glTexCoord2f(0, 1);
    glVertex3f(0, texture.getHeight(), 0);

    glTexCoord2f(1, 1);
    glVertex3f(texture.getWidth(), texture.getHeight(), 0);

    glTexCoord2f(1, 0);
    glVertex3f(texture.getWidth(), 0, 0);
    glEnd();
  }

  private void renderFlat() {
    Graphics2D g = texture.getGraphics();
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0));
    g.clearRect(0, height - boxHeight, width, height);
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1));
    g.setPaint(Color.black);
    g.fillRect(0, height - boxHeight, width, boxHeight);
    g.setColor(Color.white);
    
    int offset = lineSpacing;
    for(String text: texts) {
      g.drawString(text, leftMargin, height - boxHeight + offset);
      offset += lineSpacing;
    }
    g.dispose();
  }

  public void setRenderSpeed(double speed) {
    renderSpeed = (0.999 * renderSpeed) + (0.001 * speed);
  }

  public void setSleepTime(double sleepTime) {
    this.sleepTime = (0.999 * this.sleepTime) + (0.001 * sleepTime);
  }

}
