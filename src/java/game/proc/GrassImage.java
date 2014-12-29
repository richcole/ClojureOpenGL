package game.proc;

import game.Context;
import game.base.textures.Image;
import game.base.textures.TextureTile;
import game.containers.Factory;

import java.util.Random;

public class GrassImage {
  
  Image image;
  Random random;
  Context context;
  
  public GrassImage(Context context) {
    this.context = context;
    this.image = new Image(128, 128);
    this.random = new Random();
    clear();
    for(int i=0;i<16;++i) {
      blade();
    }
  }
  
  public void blade() {
    double width = image.getWidth();
    double height = image.getHeight() * (0.7 + random.nextDouble() * 0.3);
    double c1 = (random.nextDouble() - 0.5) * 0.01;
    double c2 = (random.nextDouble() - 0.5) * 0.01;
    double c3 = random.nextDouble() * width;
    double t = (0.2 + random.nextDouble()*0.8) * width * 0.02;
    int color = 0xff228822;
    for(int y=1; y<Math.floor(height); ++y) {
      double dx = y*y*c1 + y*c2 + c3;
      double dt = t * (height - y) / height;
      for(int x=(int)(dx-dt); x<=(int)(dx+dt); ++x) {
        int tx = x;
        while(tx < 0) {
          tx += image.getWidth();
        }
        while(tx >= image.getWidth()) {
          tx -= image.getWidth();
        }
        double d1 = ( 0.3 + Math.abs(dx - x) ) / dt;
        int r1 = (byte) (d1 * 10);
        int r2 = (byte) (d1 * 100);
        int r3 = (byte) (d1 * 10);
        if ( image.getHeight() - 1 - y == 0 ) {
          throw new RuntimeException("HEY!");
        }
        image.setRGB(tx, image.getHeight() - 1 - y, r1 | (r2 << 8) | (r3 << 16) | 0xff000000);
      }
    }
  }
  
  public void clear() {
    int c = 0;
    for(int y=1;y<image.getHeight(); ++y) {
      for(int x=0;x<image.getWidth(); ++x) {
        image.setRGB(x, y, c);
      }
    }
  }
  
  public TextureTile getTextureTile() {
    return context.getTilingTextures().getTexture("grass", new Factory<Image>() {
      @Override
      public Image create() {
        return image;
      }
    });
  }

}
