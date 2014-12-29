package game.models;

import game.Context;
import game.containers.CacheMap;
import game.containers.Containers;
import game.enums.TileSet;
import game.nwn.readers.set.SetReader.TileSetDescription;

public class TileSetDescriptions {
  
  Context context;
  CacheMap<String, TileSetDescription> cacheMap = Containers.newCacheMap();
  
  public TileSetDescriptions(Context context) {
    this.context = context;
  }
  
  public TileSetDescription getTileSetDescription(TileSet tileSet) {
    return cacheMap.ensure(tileSet.getResName(), new TileSetDescriptionLoader(context, tileSet));
  }

}
