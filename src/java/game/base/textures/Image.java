package game.base.textures;

import game.nwn.readers.BinaryFileReader;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;

import com.google.common.base.Throwables;

public class Image {

  int width;
  int height;
  BufferedImage img;

  public Image(File file) {
    try {
      img = ImageIO.read(file);
      width = img.getWidth();
      height = img.getHeight();
    } catch (Exception e) {
      throw new RuntimeException("Unable to load " + file, e);
    }
  }

  public Image(int width, int height) {
    this.width = width;
    this.height = height;
    this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
  }

  public Image(BinaryFileReader inp, long offset, int length, String ext) {
    long mark = inp.pos();
    inp.seek(offset);
    try {
      Iterator<ImageReader> it = ImageIO.getImageReadersBySuffix(ext);
      while(it.hasNext()) {
        ImageReader reader = it.next();
        reader.setInput(new FileImageInputStream(inp.inp));
        try {
          this.img = reader.read(0);
          this.width = img.getWidth();
          this.height = img.getHeight();
          return;
        }
        catch(Exception e) {
          Throwables.propagate(e);
        }
      }
    }
    finally {
      inp.seek(mark);
    }
  }
  
  public ByteBuffer getByteBuffer() {
    ByteBuffer byteBuf = ByteBuffer.allocateDirect(width*height*4);
    byteBuf.order(ByteOrder.nativeOrder());
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        int rgb = img.getRGB(x, y);
        byteBuf.put((byte) ((rgb >> 16) & 0xff));
        byteBuf.put((byte) ((rgb >> 8) & 0xff));
        byteBuf.put((byte) ((rgb >> 0) & 0xff));
        byteBuf.put((byte) ((rgb >> 24) & 0xff));
      }
    }
    byteBuf.flip();
    return byteBuf;
  }
  
  public void setRGB(int x, int y, int rgb) {
    img.setRGB(x, y, rgb);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
  
  public BufferedImage getImage() {
    return img;
  }

  public void drawImage(Image image, int x, int y) {
    Graphics g = img.getGraphics();
    int h = image.getHeight();
    int w = image.getWidth();
    g.drawImage(image.getImage(), x, y, x+w, y+h, 0, 0, w, h, null);
    g.dispose();
    img.flush();
  }

  public void write(String name) {
    try {
      ImageIO.write(img, "png", new File(name));
    } catch(Exception e) {
      Throwables.propagate(e);
    }
  }

  public void flush() {
    img.flush();
  }

}
