package game.models;

import game.Context;
import game.Renderable;
import game.math.Vector;

import java.util.List;

import com.google.common.collect.Lists;

public class Grid implements Renderable {

  int gx;
  int gy;
  int tx;
  int ty;
  int gridsPerTile;
  GridSquare[] gridSquares;
  TileSquare[] tileSquares;
  double gridScale;
  double tileScale;
  Context context;
  
  Rect rect;
  
  public static class TileSquare {
    public int x;
    public int y;
    TerrainTile terrainTile;
    
    TileSquare(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public void setTerrainTile(TerrainTile terrainTile) {
      this.terrainTile = terrainTile;
    }

    public TerrainTile getTerrainTile() {
      return this.terrainTile;
    }
  }
  
  public static class GridSquare {

    public int x;
    public int y;
    public Creature creature;

    public GridSquare(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public Creature getCreature() {
      return creature;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }
  }
  
  public Grid(Context context, int dx, int dy) {
    this.context = context;
    this.gridScale = context.getGridScale();
    this.tileScale = context.getTileScale();
    this.gridsPerTile = (int)(tileScale/gridScale);
    this.gx = dx;
    this.gy = dy;
    this.tx = dx/gridsPerTile;
    this.ty = dy/gridsPerTile;
    Vector n = Vector.NORMAL.times(gridScale/2);
    Vector l = Vector.LEFT.times(gridScale/2);
    this.rect = new Rect(Vector.Z, n, l, context.getStoneTexture());
    this.gridSquares = new GridSquare[dx*dy];
    this.tileSquares = new TileSquare[tx*ty];
    for(int j=0;j<dx; ++j) {
      for(int i=0;i<dx; ++i) {
        gridSquares[gridIndex(i, j)] = new GridSquare(i, j);
      }
    }
    for(int j=0;j<tx; ++j) {
      for(int i=0;i<ty; ++i) {
        tileSquares[tileIndex(i, j)] = new TileSquare(i, j);
      }
    }
  }

  private int gridIndex(int i, int j) {
    return j*gx + i;
  }

  private int tileIndex(int i, int j) {
    return j*tx + i;
  }

  @Override
  public void render() {
    for(GridSquare tile: gridSquares) {
      rect.setPos(new Vector((tile.x*gridScale)-(gridScale/2), (tile.y*gridScale)-(gridScale/2), -1.0, 1.0));
      rect.render();
    }
    for(TileSquare tile: tileSquares) {
      if ( tile.terrainTile != null ) {
        tile.terrainTile.setPos(new Vector(tile.x*tileScale+tileScale/2, tile.y*tileScale+tileScale/2, 0, 1.0));
        tile.terrainTile.render();
      }
    }
  }
  
  public Vector center(int x, int y) {
    GridSquare tile = gridSquares[gridIndex(x,y)];
    return center(tile);
  }

  public Vector center(GridSquare tile) {
    return new Vector((tile.x)*gridScale, (tile.y)*gridScale, 0, 1.0);
  }

  public void register() {
    context.getScene().register(this);
  }

  public GridSquare addCreature(Creature creature, Vector pos) {
    GridSquare tile = getGridSquareAt(pos);
    for(int i=0;i<10;++i) {
      for(GridSquare cand: gridNeighbourhood(tile, i)) {
        if ( cand.creature == null ) {
          cand.creature = creature;
          return cand;
        }
      }
    }
    return null;
  }

  public GridSquare getGridSquareAt(Vector pos) {
    int x = (int) Math.floor(pos.x() / gridScale);
    int y = (int) Math.floor(pos.y() / gridScale);
    if ( x >= 0 && x < gx && y >= 0 && y < gy ) {
      return gridSquares[gridIndex(x,y)];
    }
    return null;
  }
  
  public TileSquare getTileSquareAt(Vector pos) {
    int x = (int) Math.floor(pos.x() / tileScale);
    int y = (int) Math.floor(pos.y() / tileScale);
    if ( x >= 0 && x < tx && y >= 0 && y < ty ) {
      return tileSquares[tileIndex(x,y)];
    }
    return null;
  }

  List<GridSquare> gridNeighbourhood(GridSquare tile, int i) {
    List<GridSquare> r = Lists.newArrayList();
    if ( i == 0 ) {
      r.add(tile); 
    } else {
      for(int sx=-i; sx<=i; ++sx) {
        for(int sy=-i; sy<=i; sy+=i*2) {
          addTile(r, tile, sx, sy);
        }      
      }
      for(int sy=-i+1; sy<i; ++sy) {
        for(int sx=-i; sx<=i; sx+=i*2) {
          addTile(r, tile, sx, sy);
        }      
      }
    }
    return r;
  }

  private void addTile(List<GridSquare> r, GridSquare tile, int sx, int sy) {
    int ax = tile.x+sx;
    int ay = tile.y+sy;
    if ( ax >= 0 && ax < gx && ay >= 0 && ay < gy ) {
      r.add(gridSquares[gridIndex(ax, ay)]);
    }
  }

  public void moveCreature(Creature creature, GridSquare oldTile, GridSquare newTile) {
    oldTile.creature = null;
    newTile.creature = creature;
  }

  public TileSquare getTileSquare(int i, int j) {
    return tileSquares[tileIndex(i, j)];
  }

}
