package game.main;

import game.Context;
import game.nwn.readers.KeyReader;
import game.nwn.readers.Resource;
import game.nwn.readers.ResourceType;

import java.io.File;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

public class ExtractResources {
  
  private static final Logger logger = Logger.getLogger(ExtractResources.class);

  Context context;
  
  ExtractResources(Context context) {
    this.context = context;
  }

  public void println(String string) {
    System.out.println(string);
  }
  
  public void printlnJson(Object obj) {
    System.out.println(context.getGson().toJson(obj));
  }
  
  public static void main(String[] args) {
    new ExtractResources(new Context()).run();
  }
  
  public void run() {
    KeyReader keyReader = context.getKeyReader();
    try {
      String resourceName = "tin01";
      ResourceType resourceType = ResourceType.SET;
      Resource resource = keyReader.getResource(resourceName, resourceType);
      resource.writeEntry(new File(resourceName + "." + resourceType.name().toLowerCase()));
    } catch(Exception e) {
      Throwables.propagate(e);
    }
  }

}
