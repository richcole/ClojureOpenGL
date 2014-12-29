package game.tools;

import game.Context;
import game.math.Vector;

public class DeformTerrainTool implements Tool {

  private Context context;

  public DeformTerrainTool(Context context) {
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
      context.getDeformableTerrain().deform();
    }
  }

  @Override
  public void handleMouseMove(float dx, float dy) {
    // TODO Auto-generated method stub
    
  }
}
