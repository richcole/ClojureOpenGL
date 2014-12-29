package game.proc;

import java.util.Random;

public class Perlin {
  
  Random random = new Random();
  
  public void generate(HeightMap heightMap, double maxHeight) {
    Random random = new Random();
    int dx=heightMap.getWidth()/2;
    int dy=heightMap.getDepth()/2;
    
    while( dx > 1 ) {
      for(int x=0;x<heightMap.getWidth();x+=dx) {
        for(int y=0;y<heightMap.getDepth();y+=dy) {
          double v = (random.nextDouble() - 0.5)*dx;
          int cx = x + (dx / 2);
          int cy = y + (dy / 2);
          
          for(int rx=0; rx<heightMap.getWidth(); ++rx) {
            for(int ry=0; ry<heightMap.getDepth(); ++ry) {
              double scale = dx / (dx + Math.sqrt( (cx - rx)*(cx - rx) + (cy - ry)*(cy - ry))); 
              heightMap.set(rx, ry, v*scale + heightMap.get(rx, ry));
            }
          }
        }
      }
      dx /= 2;
      dy /= 2;      
    }
    
  }


}
