package game;

import game.tools.Tool;

import java.util.Set;

import org.apache.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.google.common.collect.Sets;

public class InputDevice {
  
  private static Logger logger = Logger.getLogger(InputDevice.class);
  
  float mx = 0, my = 0;
  float x = 0, y = 0;
  boolean haveMouseCoords = false;
  boolean quit = false;
  boolean grabbed = false;

  private Context context;
  private Set<Tool> activeTools;

  public InputDevice(Context context) {
    this.context = context;
    this.activeTools = Sets.newHashSet();
    Mouse.setGrabbed(grabbed);
  }
  
  public void makeActive(Tool tool) {
    activeTools.add(tool);
  }

  public void process() {
    if ( Display.isCloseRequested() ) {
      quit = true;
    }
    processMouse();
    processKeyboard();
  }

  private void processKeyboard() {
    while( Keyboard.next() ) {
      int key = Keyboard.getEventKey();
      boolean pressed = Keyboard.getEventKeyState();
      
      switch(key) {
      case Keyboard.KEY_ESCAPE:
        quit = true;
        break;
      default:
        for(Tool tool: activeTools) {
          tool.handleKeyboardInput(key, pressed);
        }
      }
    }
  }

  private void processMouse() {
    while( Mouse.next() ) {
      for(Tool tool: activeTools) {
        if ( Mouse.getEventDWheel() != 0 ) {
          tool.handleWheel(Mouse.getEventDWheel() > 0);
        }
        if ( Mouse.getEventButton() == 1 ) {
          grabbed = Mouse.getEventButtonState();
          Mouse.setGrabbed(grabbed);
          haveMouseCoords = false;
        }
      }

      if ( ! grabbed ) {
        boolean pressed = Mouse.getEventButtonState();
        if ( pressed ) {
          int button = Mouse.getEventButton();      
          for(Tool tool: activeTools) {
            tool.handleMouseButton(button, Mouse.getX(), Mouse.getY());
          }
        }
      }
      else {
        if ( haveMouseCoords ) {
          float dx = Mouse.getX() - mx;
          float dy = Mouse.getY() - my;
          x += dx;
          y += dy;
          for(Tool tool: activeTools) {
            tool.handleMouseMove(dx, dy);
          }
        }
        haveMouseCoords = true;
        mx = Mouse.getX();
        my = Mouse.getY();
        float minX = context.getView().getWidth() / 5;
        float minY = context.getView().getHeight() / 5;
        float maxX = context.getView().getWidth() - minX;
        float maxY = context.getView().getHeight() - minY;
        if ( mx <= minX || mx >= maxX || my <= minY || my >= maxY ) {
          haveMouseCoords = false;
          Mouse.setCursorPosition((int)context.getView().getWidth()/2, (int)context.getView().getHeight()/2);
        }
      }
    }
  }

  public boolean getQuit() {
    return quit;
  }

  public float getY() {
    return y;
  }

  public float getX() {
    return x;
  }

  public void setQuit() {
    quit = true;
  }
  
  public boolean getHaveMouseCoords() {
    return haveMouseCoords;
  }
}
