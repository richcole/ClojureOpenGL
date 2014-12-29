package game.containers;

import java.util.HashMap;

public class CacheMap<K,V> extends HashMap<K, V> {
  
  private static final long serialVersionUID = 1966836564884468329L;

  public V ensure(K k, Factory<V> f) {
    V v = get(k);
    if ( v == null ) {
      v = f.create();
      put(k, v);
    }
    return v;
  }
  
}
