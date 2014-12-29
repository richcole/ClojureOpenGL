package game.models;

import game.Context;
import game.base.textures.TextureTile;
import game.math.Vector;

import java.util.List;

import com.google.common.collect.Lists;

public class SkyBox extends Cube {
  
  final static double SIZE = 10000;
  
  public SkyBox(Context context) {
    super(context, Vector.Z, SIZE);
  }

  @Override
  protected List<TextureTile> getTextures() {
    return Lists.newArrayList(
      loadTexture("front.jpg"),
      loadTexture("back.jpg"),
      loadTexture("left.jpg"),
      loadTexture("right.jpg"),
      loadTexture("up.jpg"),
      loadTexture("down.jpg")
     );
  }

  private TextureTile loadTexture(String filename) {
    return context.getTilingTextures().getFileTexture(filename);
  }

  public void register() {
    context.getScene().register(this);
  }
}
