package game.base.textures;

import game.Context;
import game.containers.Factory;
import game.models.FileImageProvider;

import java.io.File;
import java.util.Map;

import com.google.common.collect.Maps;

public class TilingTextures {
  
  Context context;
  Map<String, TextureTile> tiles = Maps.newHashMap();
  Map<Size,   SolidTexture> textures = Maps.newHashMap();
  
  public TilingTextures(Context context) {
    this.context = context;
  }
  
  public TextureTile getTexture(String name, Factory<Image> imageFactory) {
    TextureTile tile = tiles.get(name);
    if ( tile == null ) {
      Image image = imageFactory.create();
      Size size = new Size(image.getWidth(), image.getHeight());
      SolidTexture solidTexture = textures.get(size);
      if ( solidTexture == null ) { 
        solidTexture = new SolidTexture(size.getWidth(), size.getHeight());
        textures.put(size, solidTexture);
      }
      int index = solidTexture.addImage(image);
      tile = new TextureTile(solidTexture, index);
      tiles.put(name, tile);
    }
    return tile;
  }
  
  public TextureTile getFileTexture(String name) {
    File file = context.getResFiles().getImageRes(name);
    return getTexture(name, new FileImageProvider(file));
  }
}
