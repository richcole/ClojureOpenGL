package game.nwn.readers;

import game.nwn.readers.KeyReader.KeyEntry;

import java.io.File;

import com.google.common.base.Throwables;
import com.google.common.io.Files;

public class Resource {
  private BifReader reader;
  private long offset;
  private int length;
  private KeyEntry entry;
  
  Resource(BifReader reader, long offset, int length, KeyEntry entry) {
    this.setReader(reader);
    this.setOffset(offset);
    this.setLength(length);
    this.setEntry(entry);
  }
  
  String getName() {
    return getEntry().getName();
  }

  public void writeEntry(File out) {
    byte[] bytes = getReader().getInp().readBytes(getOffset(), getLength());
    try {
      Files.write(bytes, out);
    }
    catch(Exception e) {
      Throwables.propagate(e);
    }
  }

  public BifReader getReader() {
    return reader;
  }

  public void setReader(BifReader reader) {
    this.reader = reader;
  }

  public long getOffset() {
    return offset;
  }

  public void setOffset(long offset) {
    this.offset = offset;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public KeyEntry getEntry() {
    return entry;
  }

  public void setEntry(KeyEntry entry) {
    this.entry = entry;
  }

}