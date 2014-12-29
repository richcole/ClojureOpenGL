package game.tools;

import org.apache.log4j.Logger;

import game.Context;
import game.math.Vector;
import game.voxel.OctTreeTerrain;

public class OctTreeTerrainTool implements Tool {

  static private Logger logger = Logger.getLogger(OctTreeTerrainTool.class);

  private Context context;

  public OctTreeTerrainTool(Context context) {
    this.context = context;
  }

  @Override
  public void handleKeyboardInput(int key, boolean pressed) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void handleWheel(boolean up) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void handleMouseButton(int eventButton, float x, float y) {
    if ( eventButton == 0 ) {
      Vector r = context.getSelectionRay().getSelectionRay(x, y);
      Vector p = context.getPlayer().getPos();
      Vector c = p.plus(r.normalize().times(200));
      logger.info("x=" + x + ", y=" + y + ", r=" + r + ", p=" + p);
      
      context.getOctTreeTerrain().renderSphereAt(c, 50);
    }
  }

  @Override
  public void handleMouseMove(float dx, float dy) {
    // TODO Auto-generated method stub
    
  }
}
