package game.base.textures;

public class TextureTile {
  private int index;
  private SolidTexture solidTexture;
  
  TextureTile(SolidTexture solidTexture, int index) {
    this.solidTexture = solidTexture;
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }
  
  public double getTextureZ() {
    double n = solidTexture.getDepth();
    double i = index;
    return (1+2*i)/(2*n);
  }

  public SolidTexture getSolidTexture() {
    return solidTexture;
  }

  public void setSolidTexture(SolidTexture solidTexture) {
    this.solidTexture = solidTexture;
  }
  
  public void bind() {
    solidTexture.bind();
  }
}