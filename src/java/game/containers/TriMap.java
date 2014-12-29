package game.containers;



public class TriMap<K1,K2,V> {
  
  CacheMap<K1, CacheMap<K2, V>> map = new CacheMap<K1, CacheMap<K2, V>>();
  
  public TriMap() {
  }
  
  public void put(K1 k1, K2 k2, V v) {
    CacheMap<K2, V> m2 = map.ensure(k1, new CacheMapFactory<K2,V>());
    m2.put(k2, v);
  }

  public void ensure(K1 k1, K2 k2, Factory<V> f) {
    CacheMap<K2, V> m2 = map.ensure(k1, new CacheMapFactory<K2,V>());
    m2.ensure(k2, f);
  }
}
