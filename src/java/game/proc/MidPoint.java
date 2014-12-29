package game.proc;

import java.util.Random;

public class MidPoint {
  
  Random random = new Random();
  
  public void generate(HeightMap heightMap, double maxHeight) {
    Random random = new Random();
    int x1 = 0;
    int x2 = heightMap.getWidth()-1;
    int y1 = 0;
    int y2 = heightMap.getDepth()-1;
    heightMap.set(x1, y1, random.nextDouble()*maxHeight);
    heightMap.set(x1, y2, random.nextDouble()*maxHeight);
    heightMap.set(x2, y1, random.nextDouble()*maxHeight);
    heightMap.set(x2, y2, random.nextDouble()*maxHeight);
    generate(heightMap, x1, y1, x2, y2);
  }

  private void generate(HeightMap heightMap, int x1, int y1, int x2, int y2) {
    int dx = (x2 - x1) / 2;
    int dy = (y2 - y1) / 2;
    if ( dx == 0 && dy == 0 ) {
      return;
    }
    int cx = x1 + dx;
    int cy = y1 + dy;
    setHeight(heightMap, x1, y1, x2, y2, cx, y1);
    setHeight(heightMap, x1, y1, x2, y2, x1, cy);
    setHeight(heightMap, x1, y1, x2, y2, cx, y2);
    setHeight(heightMap, x1, y1, x2, y2, x2, cy);

    setHeight(heightMap, x1, y1, x2, y2, cx, cy);

    generate(heightMap, x1,    y1,    x1+dx, y1+dy);
    generate(heightMap, x1+dx, y1,    x2,    y1+dy);
    generate(heightMap, x1,    y1+dy, x1+dx, y2);
    generate(heightMap, x1+dx, y1+dy, x2,    y2);
  }

  private void setHeight(HeightMap heightMap, int x1, int y1, int x2, int y2, int cx, int cy) {
    double h1 = heightMap.get(x1, y1);
    double h2 = heightMap.get(x2, y2);
    double h3 = heightMap.get(x1, y2);
    double h4 = heightMap.get(x2, y1);
    double h = ((h1 + h2 + h3 + h4) / 4.0) + 0.1 * (random.nextDouble() - 0.5) * ( x2 + y2 - x1 - y1 );
    heightMap.setIfNotSet(cx, cy, h);
  }

}
