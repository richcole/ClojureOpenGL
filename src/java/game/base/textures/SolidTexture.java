package game.base.textures;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.collect.Lists;

public class SolidTexture implements Texture {
  int        width;
  int        height;
  int        depth;
  int        textureId;
  List<ByteBuffer> buffers = Lists.newArrayList();
  
  public SolidTexture(int width, int height) {
    this.width = width;
    this.height = height;
    this.depth = 0;
    allocateTextureId();
    bind();
    GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
    GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
    GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
  }
  
  public int addImage(Image img) {
    depth += 1;
    if ( width != img.getWidth() || height != img.getHeight() ) {
      throw new RuntimeException("Incompatble image");
    }
    buffers.add(img.getByteBuffer());
    reallocateTexture();
    return depth - 1;
  }

  private void reallocateTexture() {
    int len = width*height*4*buffers.size();
    ByteBuffer bigBuf = ByteBuffer.allocateDirect(len);
    for(ByteBuffer smallBuf: buffers) {
      smallBuf.position(0);
      bigBuf.put(smallBuf);
    }
    bigBuf.flip();
    bind();
    GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0, GL11.GL_RGBA, width, height, depth, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bigBuf);
  }

  private void allocateTextureId() {
    IntBuffer textureIdBuf = BufferUtils.createIntBuffer(1);
    GL11.glGenTextures(textureIdBuf);
    textureId = textureIdBuf.get(0);
    if ( textureId == 0 ) {
      throw new RuntimeException("Unable to allocate texture.");
    }
  }
  
  public void bind() {
    GL11.glBindTexture(GL12.GL_TEXTURE_3D, textureId);
  }

  public int getDepth() {
    return buffers.size();
  }

}
