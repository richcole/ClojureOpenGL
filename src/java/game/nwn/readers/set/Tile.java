package game.nwn.readers.set;

import java.util.List;

import com.google.common.collect.Lists;

public class Tile {
  String model;
  String walkMesh;
  String topLeft;
  String topLeftHeight;
  String topRight;
  String topRightHeight;
  String bottomLeft;
  String bottomLeftHeight;
  String bottomRight;
  String bottomRightHeight;
  String top;
  String right;
  String bottom;
  String left;
  String mainLight1;
  String mainLight2;
  String sourceLight1;
  String sourceLight2;
  String animLoop1;
  String animLoop2;
  String animLoop3;
  String sounds;
  String pathNode;
  String orientation;
  String imageMap2D;
  String doorVisibilityNode;
  String doorVisibilityOrientation;
  String visibilityNode;
  String visibilityOrientation;

  @Name("doors")
  Integer numDoors;
  
  @Name("doorsList")
  List<Door> doors = Lists.newArrayList();

  public String getModel() {
    return model;
  }

  public String getWalkMesh() {
    return walkMesh;
  }

  public String getTopLeft() {
    return topLeft;
  }

  public String getTopLeftHeight() {
    return topLeftHeight;
  }

  public String getTopRight() {
    return topRight;
  }

  public String getTopRightHeight() {
    return topRightHeight;
  }

  public String getBottomLeft() {
    return bottomLeft;
  }

  public String getBottomLeftHeight() {
    return bottomLeftHeight;
  }

  public String getBottomRight() {
    return bottomRight;
  }

  public String getBottomRightHeight() {
    return bottomRightHeight;
  }

  public String getTop() {
    return top;
  }

  public String getRight() {
    return right;
  }

  public String getBottom() {
    return bottom;
  }

  public String getLeft() {
    return left;
  }

  public String getMainLight1() {
    return mainLight1;
  }

  public String getMainLight2() {
    return mainLight2;
  }

  public String getSourceLight1() {
    return sourceLight1;
  }

  public String getSourceLight2() {
    return sourceLight2;
  }

  public String getAnimLoop1() {
    return animLoop1;
  }

  public String getAnimLoop2() {
    return animLoop2;
  }

  public String getAnimLoop3() {
    return animLoop3;
  }

  public String getSounds() {
    return sounds;
  }

  public String getPathNode() {
    return pathNode;
  }

  public String getOrientation() {
    return orientation;
  }

  public String getImageMap2D() {
    return imageMap2D;
  }

  public String getDoorVisibilityNode() {
    return doorVisibilityNode;
  }

  public String getDoorVisibilityOrientation() {
    return doorVisibilityOrientation;
  }

  public String getVisibilityNode() {
    return visibilityNode;
  }

  public String getVisibilityOrientation() {
    return visibilityOrientation;
  }

  public Integer getNumDoors() {
    return numDoors;
  }

  public List<Door> getDoors() {
    return doors;
  }
}
