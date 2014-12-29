package game.imageio;

import game.base.textures.Image;
import game.nwn.readers.BinaryFileReader;

public class TgaLoader {
  
  public TgaLoader() {
  }

  public static class Header {
    long idLength;
    long colorMapType;
    long imageType;
    byte[] id;
    public int colorMapOffset;
    public int colorMapLength;
    public int colorMapEntrySize;
    public int imageX;
    public int imageY;
    public int width;
    public int height;
    public int imageDepth;
    public int descriptor;
    public byte[] colorMap;
    public byte[] pixels;
  }
  
  public Image readImage(BinaryFileReader inp, long offset) {
    long mark = inp.pos();
    inp.seek(offset);

    Header header = new Header();
    header.idLength =  inp.readByte();
    header.colorMapType = inp.readByte();
    header.imageType =  inp.readByte();
    
    if ( header.imageType != 2 ) {
      throw new RuntimeException("Unsupported image type");
    }
    
    header.colorMapOffset = inp.readShort();
    header.colorMapLength = (int) inp.readShort();
    header.colorMapEntrySize = (int) inp.readByte();
    header.imageX = (int) inp.readShort();
    header.imageY = (int) inp.readShort();
    header.width = (int) inp.readShort();
    header.height = (int) inp.readShort();
    header.imageDepth = (int) inp.readByte();
    header.descriptor = (int) inp.readByte();
    header.id = inp.readBytes((int)header.idLength);
    if (header.colorMapType == 1) {
      header.colorMap = inp.readBytes(header.colorMapLength * header.colorMapEntrySize / 8);
    }
    header.pixels = inp.readBytes(header.width * header.height * header.imageDepth / 8);
    Image image = new Image(header.width, header.height);
    for(int j=0;j<header.height;++j) {
      for(int i=0;i<header.width;++i) {
        int x = (j*header.width+i)*(header.imageDepth / 8);
        int r = (header.pixels[x  ] & 0xFF) << 0;
        int g = (header.pixels[x+1] & 0xFF) << 8;
        int b = (header.pixels[x+2] & 0xFF) << 16;
        int a = (0xFF) << 24;
        image.setRGB(i, j, r | g | b | a);
      }
    }
    inp.seek(mark);
    image.flush();
    return image;
  }
  
}
