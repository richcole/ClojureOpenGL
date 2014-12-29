package game.tools;

import game.Context;
import game.Player;
import game.enums.TileSet;
import game.math.Vector;
import game.models.Creature;
import game.models.TerrainTile;
import game.models.Grid.GridSquare;
import game.models.Grid.TileSquare;
import game.nwn.readers.set.SetReader.TileSetDescription;

import org.lwjgl.input.Mouse;

public class SelectedCreatureTool implements Tool {

  private Creature selectedCreature;
  private Context context;
  private TileSquare selectedTileSquare;
  private TileSetDescription tileSetDescription;
  
  SelectedCreatureTool(Context context) {
    this.context = context;
    this.tileSetDescription = context.getTileSetDescriptions().getTileSetDescription(TileSet.Tin01);
    this.selectedTileSquare = context.getTerrain().getTileSquare(0, 0);
  }

  public void setSelectedCreature(Creature selectedCreature) {
    if ( this.selectedCreature != null ) {
      this.selectedCreature.setSelected(false);
    }
    if ( selectedCreature != null ) {
      selectedCreature.setSelected(true);
    }
    this.selectedCreature = selectedCreature;
  }

  public Creature getSelectedCreature() {
    return selectedCreature;
  }
  
  private void selectTerrainCreature(float x2, float y) {
    Vector f = context.getSelectionRay().getSelectionRay(x2, y);
    Player player = context.getPlayer();
    Vector p = player.getPos();
    double s = -p.z() / f.z();
    Vector x = p.plus(f.times(s));
    GridSquare tile = context.getTerrain().getGridSquareAt(x);
    if ( tile  != null ) {
      setSelectedCreature(tile.getCreature());
    } else {
      setSelectedCreature(null);
    }
    
    TileSquare tileSquare = context.getTerrain().getTileSquareAt(x);
    if ( tileSquare != null ) {
      setSelectedTileSquare(tileSquare);
    } else {
      setSelectedTileSquare(null);
    }
  }

  @Override
  public void handleKeyboardInput(int key, boolean pressed) {
    // nothing to do
  }

  @Override
  public void handleWheel(boolean up) {
    if ( up ) {
      nextTerrainTileIndex();
    }
    else {
      prevTerrainTileIndex();
    }    
  }

  @Override
  public void handleMouseButton(int button, float x, float y) {
    switch( button ) {
    case 0:
      selectTerrainCreature(x, y);
      break;
    }
  }

  @Override
  public void handleMouseMove(float dx, float dy) {
    // do nothing
  }

  public void nextTerrainTileIndex() {
    updateSelectedTile(1);
  }
  
  public void prevTerrainTileIndex() {
    updateSelectedTile(-1);
  }

  private void updateSelectedTile(int dirn) {
    if ( selectedTileSquare != null ) {
      TerrainTile tile = selectedTileSquare.getTerrainTile();
      if ( tile == null ) {
        tile = context.newTile();
        selectedTileSquare.setTerrainTile(tile);
        tile.setModel(tileSetDescription.getTiles().get(0).getModel());
        tile.setModelIndex(0);
      } else {
        int numTiles = tileSetDescription.getTiles().size();
        int modelIndex = tile.getModelIndex() + dirn;
        if ( modelIndex < 0 ) {
          modelIndex = numTiles + modelIndex;
        } else if ( modelIndex >= numTiles ) {
          modelIndex = modelIndex - numTiles;
        }
        tile.setModel(tileSetDescription.getTiles().get(modelIndex).getModel());
        tile.setModelIndex(modelIndex);
      }
    }
  }

  public void setSelectedTileSquare(TileSquare selectedTileSquare) {
    this.selectedTileSquare = selectedTileSquare;
  }
  

}
