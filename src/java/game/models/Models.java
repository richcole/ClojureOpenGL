package game.models;

import game.Context;
import game.base.io.Serializer;
import game.containers.CacheMap;
import game.containers.Containers;
import game.containers.Factory;
import game.enums.Model;

import java.io.File;

public class Models {
  
  public CacheMap<String, AnimMesh> animMeshes = Containers.newCacheMap();
  public CacheMap<String, CompressedAnimMesh> compressedAnimMeshes = Containers.newCacheMap();
  
  Context context;
  
  public Models(Context context) {
    this.context = context;
  }
  
  class AnimMeshFactory implements Factory<AnimMesh> {
    
    private File resFile;

    AnimMeshFactory(File resFile) {
      this.resFile = resFile;
    }

    @Override
    public AnimMesh create() {
      Serializer serializer = new Serializer();
      return serializer.deserialize(resFile, AnimMesh.class);
    }
    
  }
  
  class CompressedAnimMeshFactory implements Factory<CompressedAnimMesh> {
    
    private String resName;
    private File resFile;

    CompressedAnimMeshFactory(String resName, File resFile) {
      this.resName = resName;
      this.resFile = resFile;
    }

    @Override
    public CompressedAnimMesh create() {
      return new CompressedAnimMesh(context, getAnimMesh(resName, resFile));
    }
    
  }

  public AnimMesh getAnimMesh(String resName, File resFile) {
    return animMeshes.ensure(resName, new AnimMeshFactory(resFile));
  }
  
  public CompressedAnimMesh getCompressedAnimMesh(String resName, File resFile) {
    return compressedAnimMeshes.ensure(resName, new CompressedAnimMeshFactory(resName, resFile));
  }

  public CompressedAnimMesh getCompressedAnimMesh(String resName) {
    return getCompressedAnimMesh(resName, context.getResFiles().getResFile(resName, "mdl"));
  }
  
  public AnimMesh getAnimMesh(String resName) {
    return getAnimMesh(resName, context.getResFiles().getResFile(resName, "mdl"));
  }

  public CompressedAnimMesh getCompressedAnimMesh(Model model) {
    String resName = model.getResName();
    File resFile = context.getResFiles().getResFile(model);
    return getCompressedAnimMesh(resName, resFile);
  }

  public AnimMesh getAnimMesh(Model model) {
    String resName = model.getResName();
    File resFile = context.getResFiles().getResFile(model);
    return getAnimMesh(resName, resFile);
  }
  
}
