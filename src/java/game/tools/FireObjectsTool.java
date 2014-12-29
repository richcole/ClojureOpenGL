package game.tools;

import game.Context;

import org.lwjgl.input.Keyboard;


public class FireObjectsTool implements Tool {
  
  Context context;
  
  public FireObjectsTool(Context context) {
    this.context = context;
  }

  public void handleKeyboardInput(int key, boolean pressed) {
    switch(key) {
    case Keyboard.KEY_SPACE:
      if ( pressed ) {
        context.getPlayer().fire();
      }
      break;
    case Keyboard.KEY_L:
      if ( pressed ) {
        context.getPlayer().fireLight();
      }
      break;
    }
  }

  @Override
  public void handleWheel(boolean up) {
    // do nothing
    
  }

  @Override
  public void handleMouseButton(int eventButton, float x, float y) {
    // do nothing
    
  }

  @Override
  public void handleMouseMove(float dx, float dy) {
    // do nothing
    
  }
}
