package game.ds;

import game.nwn.readers.BinaryFileReader;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Parser {
  
  BinaryFileReader reader;
  Chunk root;
  
  static Set<Integer> groupChunkTypes = Sets.newHashSet(0x4d4d, 0x3d3d, 0xafff, 0x7012, 0x7011, 0x7020);
  
  interface ChunkData {
  }
  
  static public class Chunk {
    ChunkType type;
    long offset;
    long length;
    List<Chunk> chunks;
    byte[] data;
    String name;
    private int id;
    
    public Chunk() {
    }
    
    public void read(BinaryFileReader reader, ChunkType parentType) {
      offset = reader.pos();
      id = reader.readShort();
      type = ChunkType.getChunkType(id, parentType);
      length = reader.readWord();
      chunks = Lists.newArrayList();
      readData(reader);
      if ( type.hasChildren ) {
        readChunks(reader, type);
      }
      reader.seek(offset + length);
    }

    private void readData(BinaryFileReader reader) {
      if ( type.dataSize > 0 ) {
        data = reader.readBytes(type.dataSize);
      } else if ( type.dataSize == -1 ) {
        name = reader.readNullString(256);
      }
    }
    
    public String toString() {
      return String.format("id=%04x type=%s length=%d", id, type.name(), length);
    }
    
    public void readChunks(BinaryFileReader reader, ChunkType parentType) {
      while( reader.pos() < offset + length ) {
        Chunk chunk = new Chunk();
        chunk.read(reader, parentType);
        chunks.add(chunk);
      }
    }

    public long getOffset() {
      return offset;
    }

    public void setOffset(long offset) {
      this.offset = offset;
    }

    public long getLength() {
      return length;
    }

    public void setLength(long length) {
      this.length = length;
    }

    public List<Chunk> getChunks() {
      return chunks;
    }
  }
  
  public Parser(File file) {
    reader = new BinaryFileReader(file);
    root = new Chunk();
    root.read(reader, null);
  }

  public Chunk getRoot() {
    return root;
  }

}
