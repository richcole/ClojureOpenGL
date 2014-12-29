package game.main;

import game.Context;
import game.base.io.Serializer;
import game.enums.Model;
import game.enums.TileSet;
import game.models.AnimMesh;
import game.nwn.NwnMesh;
import game.nwn.readers.MdlReader;
import game.nwn.readers.Resource;
import game.nwn.readers.ResourceType;
import game.nwn.readers.set.SetReader;
import game.nwn.readers.set.Tile;

import java.io.File;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;

public class ExtractModels {
  
  private static final Logger logger = Logger.getLogger(ExtractModels.class);

  Context context;
  Set<String> visitedResources = Sets.newHashSet();
  
  ExtractModels(Context context) {
    this.context = context;
  }

  public static void main(String[] args) {
    new ExtractModels(new Context()).run(args);
  }
  
  public void run(String[] args) {
    if ( args.length > 0 ) {
      context.getResFiles().setRootDirectory(new File(args[0]));
    }
    Serializer serializer = new Serializer();
    for(Model model: Model.values()) {
      extractModel(serializer, model);
    }
    SetReader setReader = new SetReader();
    for(TileSet tileSet: TileSet.values()) {
      Resource res = context.getKeyReader().getResource(tileSet.getResName(), ResourceType.SET);
      game.nwn.readers.set.SetReader.TileSetDescription ts = setReader.read(res);
      serializer.serialize(ts, context.getResFiles().getResFile(tileSet));
      for(Tile tile: ts.getTiles()) {
        extractModel(serializer, tile.getModel());
      }
    }
  }

  private void extractModel(Serializer serializer, Model model) {
    String resName = model.getResName();
    extractModel(serializer, resName);
  }

  private void extractModel(Serializer serializer, String resName) {
    try {
      File resFile = context.getResFiles().getResFile(resName, "mdl");
      if ( haveVisitedResource(resFile.getName()) ) {
        return;
      }
      MdlReader mdlReader = context.getKeyReader().getMdlReader(resName);
      NwnMesh mesh = new NwnMesh(context, mdlReader.readModel(), 0);
      AnimMesh animMesh = mesh.getAnimMesh();
      logger.info("Writing " + resFile.getCanonicalPath());
      serializer.serialize(animMesh, resFile);
      for(String textureName: animMesh.getTextures()) {
        try {
          String textureResFileName = "res/" + textureName + ".tga";
          if ( haveVisitedResource(textureResFileName) ) {
            continue;
          }
          Resource textureResource = context.getKeyReader().getResource(textureName, ResourceType.TGA);
          logger.info("Writing " + textureResFileName);
          textureResource.writeEntry(new File(textureResFileName));
        } catch(Exception e) {
          logger.error("Unable to locate texture: " + textureName);
        }
      }
    } catch(Exception e) {
      Throwables.propagate(e);
    }
  }

  public boolean haveVisitedResource(String res) {
    if ( visitedResources.contains(res) ) {
      return true;
    } else {
      visitedResources.add(res);
      return false;
    }
  }
}
