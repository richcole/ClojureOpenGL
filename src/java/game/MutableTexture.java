package game;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
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
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import game.base.textures.Image;

import java.awt.Graphics2D;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class MutableTexture {

  int textureId;
  Image img;
  ByteBuffer byteBuf;
  
  MutableTexture(int width, int height) {
    img = new Image(width, height);
    byteBuf = ByteBuffer.allocateDirect(width*height*4);
    allocateTextureId();
    glBindTexture(GL_TEXTURE_2D, textureId);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
  }
  
  private ByteBuffer getByteBuffer(int[] buf) {
    byteBuf.rewind();
    byteBuf.order(ByteOrder.nativeOrder());
    byteBuf.rewind();
    for(int i=0;i<buf.length;++i) {
      byteBuf.put((byte)(buf[i] >> 0));
      byteBuf.put((byte)(buf[i] >> 8));
      byteBuf.put((byte)(buf[i] >> 16));
      byteBuf.put((byte)(buf[i] >> 24));
    }
    byteBuf.flip();
    return byteBuf;
  }

  private void updateTexture(int width, int height, ByteBuffer byteBuf) {
    glBindTexture(GL_TEXTURE_2D, textureId);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuf);
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
  
  public void update() {
    DataBufferInt dataBuffer = (DataBufferInt) img.getImage().getAlphaRaster().getDataBuffer();
    updateTexture(img.getWidth(), img.getHeight(), getByteBuffer(dataBuffer.getData()));
  }
  
  public Graphics2D getGraphics() {
    return img.getImage().createGraphics();
  }

  public float getWidth() {
    return img.getWidth();
  }

  public float getHeight() {
    return img.getHeight();
  }

  public Image getImage() {
    return img;
  }

}
