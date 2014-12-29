package game.tools;

public interface Tool {

  void handleKeyboardInput(int key, boolean pressed);
  void handleWheel(boolean up);
  void handleMouseButton(int eventButton, float x, float y);
  void handleMouseMove(float dx, float dy);

}
