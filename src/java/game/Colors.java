package game;
import game.math.Vector;

import java.nio.FloatBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;

import com.google.common.collect.Maps;


public class Colors {

  private FloatBuffer white = getColor(1f, 1f, 1f, 1f);
  private FloatBuffer gray2 = getColor(0.2f, 0.2f, 0.2f, 0.2f);
  private FloatBuffer gray9 = getColor(0.9f, 0.9f, 0.9f, 0.9f);
  
  HashMap<Vector, FloatBuffer> colorMap = Maps.newHashMap();
  
  Colors() {
  }

  public FloatBuffer getColor(float r, float g, float b, float a) {
    FloatBuffer buf = BufferUtils.createFloatBuffer(4);
    buf.put(r).put(g).put(b).put(a).flip();
    return buf;
  }

  public FloatBuffer getColor(Vector v) {
    FloatBuffer buf = colorMap.get(v);
    if ( buf == null ) {
      buf = BufferUtils.createFloatBuffer(4);
      for(int i=0;i<4;++i) {
        buf.put((float)v.get(i));
      }
      buf.flip();
      colorMap.put(v, buf);
    }
    return buf;
  }

  public FloatBuffer getWhite() {
    return white;
  }

  public FloatBuffer getGray9() {
    return gray9;
  }
  
  public FloatBuffer getGray2() {
    return gray9;
  }


}
