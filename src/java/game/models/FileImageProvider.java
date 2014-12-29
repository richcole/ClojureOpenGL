package game.models;

import game.base.textures.Image;
import game.imageio.TgaLoader;
import game.nwn.ImageProvider;
import game.nwn.readers.BinaryFileReader;

import java.io.File;

public class FileImageProvider implements ImageProvider {

  private File file;

  public FileImageProvider(File file) {
    this.file = file;
  }
  
  @Override
  public Image create() {
    if ( file.getName().endsWith(".tga") ) {
      TgaLoader imageLoader = new TgaLoader();
      BinaryFileReader reader = new BinaryFileReader(file);
      try {
        return imageLoader.readImage(reader, 0);
      } finally {
        reader.close();
      }
    } else {
      return new Image(file);
    }
  }

}
