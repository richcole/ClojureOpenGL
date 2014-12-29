package game.base.textures;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_MODE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexEnvf;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ImageTexture implements Texture {
  int width;
  int height;
  int textureId;
  
  public ImageTexture(File file) {
    this(new Image(file));
  }
  
  public ImageTexture(Image img) {
    img.write("out.png");
    width = img.getWidth();
    height = img.getHeight();
    allocateTexture(width, height, img.getByteBuffer());
  }

  private void allocateTexture(int width, int height, ByteBuffer byteBuf) {
    allocateTextureId();
    glBindTexture(GL_TEXTURE_2D, textureId);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL11.GL_RGBA, GL_UNSIGNED_BYTE, byteBuf);
  }

  private void allocateTextureId() {
    IntBuffer textureIdBuf = BufferUtils.createIntBuffer(1);
    glGenTextures(textureIdBuf);
    textureId = textureIdBuf.get(0);
    if ( textureId == 0 ) {
      throw new RuntimeException("Unable to allocate texture.");
    }
  }
  
  public void bind() {
    glBindTexture(GL_TEXTURE_2D, textureId);
  }

  public void replaceImage(Image img) {
    width = img.getWidth();
    height = img.getHeight();
    glBindTexture(GL_TEXTURE_2D, textureId);
    glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, img.getByteBuffer());
  }

}
