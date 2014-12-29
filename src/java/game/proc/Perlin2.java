package game.proc;

import java.nio.FloatBuffer;
import java.util.List;

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;

import com.google.common.collect.Lists;

public class Perlin2 {
  
  private static Logger logger = Logger.getLogger(Perlin2.class);
  
  List<Signal> signals;
  
  public Perlin2(int numSignals) {
    signals = Lists.newArrayList();
    for(int i=4;i<=numSignals; ++i) {
      signals.add(new Signal(i));
    }
  }
  
  double get(double x, double y, double v) {
    double r = 0;
    for(Signal signal: signals) {
      r += signal.get(x, y, v);
    }
    return r;
  }
  
  public void generate(HeightMap heightMap, double maxHeight) {
    double w = heightMap.getWidth();
    double d = heightMap.getDepth();
    for(int x=0;x<w;x++) {
      for(int y=0;y<d;y++) {
        heightMap.set(x, y, get(x / w, y / d, maxHeight));
      }
    }
  }
  
  public FloatBuffer toFloatArray(double w, double d, double h) {
    FloatBuffer buf = BufferUtils.createFloatBuffer((int)(w*d));
    for(int x=0;x<w;x++) {
      for(int y=0;y<d;y++) {
        float v = (float)get(x / w, y / d, h);
        buf.put(v);
      }
    }
    buf.flip();
    return buf;
  }

}
