package game.base.textures;

import game.Context;
import game.containers.Factory;
import game.models.FileImageProvider;

public class FileTextureFactory implements Factory<Texture> {
  
  private String name;
  private Context context;

  FileTextureFactory(Context context, String name) {
    this.name = name;
    this.context = context;
  }

  @Override
  public Texture create() {
    Image image = new FileImageProvider(context.getResFiles().getImageRes(name)).create();
    return new ImageTexture(image);
  }

}
