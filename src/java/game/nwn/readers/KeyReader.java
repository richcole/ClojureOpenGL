package game.nwn.readers;

import game.Context;
import game.base.textures.Image;
import game.imageio.TgaLoader;
import game.nwn.readers.BifReader.EntryHeader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class KeyReader {
  
  private static Logger logger = Logger.getLogger(KeyReader.class);
  
  Context context;
  Map<String, Map<Integer, BifReader>> bifReaders = Maps.newHashMap();
  Multimap<String, Resource> keyIndex = Multimaps.newListMultimap(Maps.<String, Collection<Resource>>newHashMap(), new ListSupplier<Resource>());
  Map<String, Image> imageMap = Maps.newHashMap();
  Map<String, MdlModel> modelMap = Maps.newHashMap();

  public KeyReader(Context context) {
    this.context = context;
    try {
      for(File keyFile: context.getNwnRoot().listFiles()) {
        String keyFileName = keyFile.getName();
        if ( keyFileName.endsWith(".key") ) {
          Map<Integer, BifReader> emptyMap = Maps.newHashMap();
          bifReaders.put(keyFileName, emptyMap);
          BinaryFileReader inp = new BinaryFileReader(keyFile);
          Header header = readHeader(inp);
          readKeyIndex(keyFileName, header, inp);
        }
      }
    } catch(Exception e) {
      Throwables.propagate(e);
    }
  }
  
  private void readKeyIndex(String keyName, Header header, BinaryFileReader inp) {
    for(int i=0;i<header.numKeys;++i) {
      KeyReader.KeyEntry entry = readKeyEntry(header, inp, i);
      keyIndex.put(entry.getName(), createResource(keyName, header, inp, entry));
    }
  }

  public BifReader getBifReader(String keyName, Header header, BinaryFileReader inp, int i) {
    Map<Integer, BifReader> bifReadersForKey = bifReaders.get(keyName);
    BifReader bifReader = bifReadersForKey.get(i);
    if ( bifReader == null ) {
      KeyReader.BifEntry entry = readBifEntry(header, inp, i);
      File bifFile = new File(context.getNwnRoot(), entry.name);
      bifReader = new BifReader(entry, bifFile);
      bifReadersForKey.put(i, bifReader);
    }
    return bifReader;
  }
  
  public Image getImage(String name) {
    Image image = imageMap.get(name);
    if ( image == null ) {
      Resource r = getResource(name, ResourceType.TGA);
      TgaLoader imageLoader = new TgaLoader();
      image = imageLoader.readImage(r.getReader().getInp(), r.getOffset());
      imageMap.put(name, image);
    }
    return image;
  }
  
  public void getWAV(String name) {
    Resource r = getResource(name, ResourceType.WAV);
    byte[] bytes = r.getReader().getInp().readBytes(r.getOffset(), r.getLength());
    try {
      AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes));
    } catch(Exception e) {
      throw new RuntimeException("Couldn't load resource " + name, e);
    }
    
  }
   
  public MdlReader getMdlReader(String name) {
    MdlReader mdlReader = new MdlReader(this, getResource(name, ResourceType.MDL));
    return mdlReader;
  }
  
  public Resource getResource(String name, ResourceType type) {
    for(Resource r: keyIndex.get(name.toLowerCase())) {
      if (r.getEntry().getType() == type.getId()) {
        return r;
      }
    }
    for(Resource r: keyIndex.values()) {
      if ( r.getName().equalsIgnoreCase(name) ) {
        logger.info("File " + r.getName() + " type=" + r.getEntry().getType());
      }
    }
    throw new RuntimeException("Unable to find resource with name " + name + " and type " + type);
  }
  
  private Resource createResource(String keyName, Header header, BinaryFileReader inp, KeyReader.KeyEntry entry) {
    int bifIndex = entry.getBifIndex();
    int resourceIndex = entry.getResourceIndex();
    BifReader bifReader = getBifReader(keyName, header, inp, bifIndex);
    EntryHeader entryHeader = bifReader.readEntryHeader(resourceIndex); 
    return new Resource(bifReader, entryHeader.offset, (int)entryHeader.size, entry);
  }
  
  static public class Header {
    String type;
    String version;
    long numBif;
    long numKeys;
    long fileTableOffset;
    long keyTableOffset;
    long buildYear;
    long buildDay;
    byte[] reserved;
  }
  
  static public class BifEntry {
    long fileSize;
    long nameOffset;
    int  nameSize;
    int  drive;
    String name;
  }

  static public class KeyEntry {
    private String name;
    private int    type;
    long   ids;
    
    int getBifIndex() {
      return (int)(ids >> 20);
    }
    
    int getResourceIndex() {
      return (int)(ids & 0xfffff);
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getType() {
      return type;
    }

    public void setType(int type) {
      this.type = type;
    }
  }
  
  public Header readHeader(BinaryFileReader inp) {
    Header header = new Header();
    header.type = inp.readString(4);
    header.version = inp.readString(4);
    header.numBif = inp.readWord();
    header.numKeys = inp.readWord();
    header.fileTableOffset = inp.readWord();
    header.keyTableOffset = inp.readWord();
    header.buildYear = inp.readWord();
    header.buildDay = inp.readWord();
    header.reserved = inp.readBytes(32);
    return header;
  }
  
  public BifEntry readBifEntry(Header header, BinaryFileReader inp, int i) {
    inp.seek(header.fileTableOffset + i*12);
    BifEntry fileEntry = new BifEntry();
    fileEntry.fileSize = inp.readWord();
    fileEntry.nameOffset = inp.readWord();
    fileEntry.nameSize = inp.readShort();
    fileEntry.drive = inp.readShort();
    fileEntry.name = inp.readStringAt(fileEntry.nameOffset, fileEntry.nameSize-1).replace("\\", "/");
    return fileEntry;
  }
  
  public KeyEntry readKeyEntry(Header header, BinaryFileReader inp, int i) {
    inp.seek(header.keyTableOffset + i*22);
    KeyEntry fileEntry = new KeyEntry();
    fileEntry.setName(inp.readNullString(16));
    fileEntry.setType(inp.readShort());
    fileEntry.ids = inp.readWord();
    return fileEntry;
  }

  public MdlModel getModel(String modelName) {
    MdlModel model = modelMap.get(modelName);
    if ( model == null ) {
      MdlReader modelReader = new MdlReader(this, getResource(modelName, ResourceType.MDL));
      model = modelReader.readModel();
      modelMap.put(modelName, model);
    }
    return model;
  }
  
  public Multimap<String, Resource> getKeyIndex() {
    return keyIndex;
  }

}
