package game.main;

import game.ds.Parser;
import game.ds.Parser.Chunk;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Read3DSModel {
  
  private static Logger logger = Logger.getLogger(Read3DSModel.class);

  public static void main(String[] args) {
    BasicConfigurator.configure();
    Parser parser = new Parser(new File("/home/richcole/Downloads/grass-block.3DS"));
    print(parser.getRoot(), "");
  }
  
  public static void print(Chunk chunk, String indent) {
    logger.info(indent + chunk.toString());
    for(Chunk child: chunk.getChunks()) {
      print(child, "  " + indent);
    }
  }
}
