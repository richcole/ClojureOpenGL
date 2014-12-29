package game.nwn.readers;

public class MdlControllerKey {
  public static final int SIZE = 0xC;
  public static final long KEY_POSITION = 8;
  public static final long KEY_ORIENTATION = 20;
  public static final long KEY_SCALE = 36;

  public static final long KEY_COLOR = 76;
  public static final long KEY_RADIUS = 88;

  long type;
  int  rows;
  int  keyOffset;
  int  dataOffset;
  int  columns;
  int  pad;
}