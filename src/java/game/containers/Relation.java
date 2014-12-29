package game.containers;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;



public class Relation<K,V> {
  
  Map<K, Set<V>> map = Maps.newHashMap();
  Set<V> values = Sets.newHashSet();
  
  public Relation() {
  }
  
  public void put(K k, V v) {
    Set<V> set = map.get(k);
    if ( set == null ) {
      set = Sets.newHashSet();
      map.put(k, set);
    }
    set.add(v);
    values.add(v);
  }
  
  public Set<V> get(K k) {
    Set<V> set = map.get(k);
    if ( set == null ) {
      return Sets.newHashSet();
    } else {
      return set;
    }
  }
  
  public Set<V> values() {
    return values;
  }

  public Set<K> keySet() {
    return map.keySet();
  }
}
