package game.models;

import game.Context;
import game.containers.Factory;
import game.enums.TileSet;
import game.nwn.readers.set.SetReader.TileSetDescription;

public class TileSetDescriptionLoader implements Factory<TileSetDescription> {

  TileSet tileSet;
  private Context context;

  public TileSetDescriptionLoader(Context context, TileSet tileSet) {
    this.context = context;
    this.tileSet = tileSet;
  }

  @Override
  public TileSetDescription create() {
    return context.getSerializer().deserialize(context.getResFiles().getResFile(tileSet.getResName(), "set"), TileSetDescription.class);
  }

}
