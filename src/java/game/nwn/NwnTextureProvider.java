package game.nwn;

import game.Context;
import game.base.textures.Image;

public class NwnTextureProvider implements ImageProvider {

  private Context context;
  private String name;

  public NwnTextureProvider(Context context, String name) {
    this.context = context;
    this.name = name;
  }

  public Image create() {
    return context.getKeyReader().getImage(name);
  }

}
