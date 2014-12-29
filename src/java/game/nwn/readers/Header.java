package game.nwn.readers;

public class Header {
  long      zero;
  long      dataOffset;
  long      dataSize;
  private MdlModel  model;
  public MdlModel getModel() {
    return model;
  }
  public void setModel(MdlModel model) {
    this.model = model;
  }
}