package game.nwn.readers;

import game.nwn.readers.KeyReader.BifEntry;

import java.io.Closeable;
import java.io.File;

public class BifReader implements Closeable {
  
  String bifFileName;
  private BinaryFileReader inp;
  Header header;
  BifEntry entry;

  BifReader(BifEntry entry, File bifFile) {
    this.entry = entry;
    this.setInp(new BinaryFileReader(bifFile));
    this.header = readHeader();
  }
  
  static public class Header {
    String sig;
    String version;
    long entries;
    long tiles;
    long entryOffset;
  }
  
  static public class EntryHeader {
    long ids;
    long offset;
    long size;
    long type;
    long unknown;
  }

  public Header readHeader() {
    getInp().seek(0);
    Header header = new Header();
    header.sig = getInp().readString(4);
    header.version = getInp().readString(4);
    header.entries = getInp().readWord();
    header.tiles = getInp().readWord();
    header.entryOffset = getInp().readWord();
    return header;
  }
  
  public EntryHeader readEntryHeader(int i) {
    getInp().seek(header.entryOffset + i*16);
    EntryHeader entryHeader = new EntryHeader();
    entryHeader.ids = getInp().readWord();
    entryHeader.offset = getInp().readWord();
    entryHeader.size = getInp().readWord();
    entryHeader.type = getInp().readWord();
    return entryHeader;
  }

  public void close() {
    getInp().close();
  }

  public BinaryFileReader getInp() {
    return inp;
  }

  public void setInp(BinaryFileReader inp) {
    this.inp = inp;
  }
  
}
