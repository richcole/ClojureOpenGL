package game.main;

import game.Context;
import game.enums.TileSet;
import game.nwn.readers.KeyReader;
import game.nwn.readers.Resource;
import game.nwn.readers.ResourceType;
import game.nwn.readers.set.SetReader;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

public class ReadTileSet {
  
  private static final Logger logger = Logger.getLogger(ReadTileSet.class);

  Context context;
  
  ReadTileSet(Context context) {
    this.context = context;
  }

  public static void main(String[] args) {
    new ReadTileSet(new Context()).run();
  }
  
  public void run() {
    KeyReader keyReader = context.getKeyReader();
    try {
      String resourceName = TileSet.Tin01.getResName();
      ResourceType resourceType = ResourceType.SET;
      Resource resource = keyReader.getResource(resourceName, resourceType);
      SetReader setReader = new SetReader();
      setReader.read(resource);
    } catch(Exception e) {
      Throwables.propagate(e);
    }
  }

}
