package game.nwn.readers;

import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

public class ListSupplier<T> implements Supplier<List<T>> {
  @Override
  public List<T> get() {
    return Lists.newArrayList();
  }
}