package game.main;

import game.Context;
import game.nwn.readers.KeyReader;
import game.nwn.readers.Resource;
import game.nwn.readers.ResourceType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

public class ListModels {
  
  private static final Logger logger = Logger.getLogger(ListModels.class);

  Context context;
  
  ListModels(Context context) {
    this.context = context;
  }

  public static void main(String[] args) {
    new ListModels(new Context()).run();
  }
  
  public void run() {
    logger.info("Starting");
    KeyReader keyReader = context.getKeyReader();
    try {
      PrintStream out = new PrintStream(new FileOutputStream(new File("list")));
      try {
        for(Resource resource: keyReader.getKeyIndex().values()) {
          ResourceType type = ResourceType.getType(resource.getEntry().getType());
          out.println(resource.getEntry().getName() + " " + (type != null ? type.name() : resource.getEntry().getType()));
        }
      } finally {
        out.close();
      }
    } catch(Exception e) {
      Throwables.propagate(e);
    }
  }

}
