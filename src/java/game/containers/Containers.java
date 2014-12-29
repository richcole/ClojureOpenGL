package game.containers;


public class Containers {
  public static <K1, K2, V> TriMap<K1, K2, V> newTriMap() {
    return new TriMap<K1, K2, V>();
  }

  public static <K, V> CacheMap<K, V> newCacheMap() {
    return new CacheMap<K, V>();
  }
}
