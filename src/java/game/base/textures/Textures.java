package game.base.textures;

import game.Context;
import game.containers.CacheMap;
import game.containers.Containers;

public class Textures {

  private Context context;
  private CacheMap<String, Texture> textureMap = Containers.newCacheMap();

  public Textures(Context context) {
    this.context = context;
  }

  public Texture getFileTexture(String name) {
    return textureMap.ensure(name, new FileTextureFactory(context, name));
  }

}
