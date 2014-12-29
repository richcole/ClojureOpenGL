package game.tools;

import game.Context;

import org.lwjgl.input.Keyboard;


public class MovePlayerTool implements Tool {
  
  Context context;
  
  public MovePlayerTool(Context context) {
    this.context = context;
  }

  public void handleKeyboardInput(int key, boolean pressed) {
    switch(key) {
      case Keyboard.KEY_Q:
        context.getPlayer().setMovingUpward(pressed);
        break;
      case Keyboard.KEY_E:
        context.getPlayer().setMovingDownward(pressed);
        break;
      case Keyboard.KEY_W:
        context.getPlayer().setMovingForward(pressed);
        break;
      case Keyboard.KEY_S:
        context.getPlayer().setMovingBackward(pressed);
        break;
      case Keyboard.KEY_A:
        context.getPlayer().setMovingLeft(pressed);
        break;
      case Keyboard.KEY_D:
        context.getPlayer().setMovingRight(pressed);
        break;
    }
  }

  @Override
  public void handleWheel(boolean up) {
    // do nothing
  }

  @Override
  public void handleMouseButton(int eventButton, float x, float y) {
  }

  @Override
  public void handleMouseMove(float dx, float dy) {
    context.getPlayer().rotate(dx, dy);
  }
}
