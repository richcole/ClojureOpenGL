package game.containers;

public class CacheMapFactory<K,V> implements Factory<CacheMap<K,V>> {

  @Override
  public CacheMap<K, V> create() {
    return new CacheMap<K, V>();
  }

}
