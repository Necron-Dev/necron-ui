package moe.nec.ui.context;

import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static yqloss.E._cast;

public class Context {
  public static class Key<T> {}

  private final Map<Key<?>, Object> map = new HashMap<>();

  public Context clear() {
    map.clear();
    return this;
  }

  public <T> Context put(Key<T> key, T value) {
    map.put(key, value);
    return this;
  }

  public boolean contains(Key<?> key) {
    return map.containsKey(key);
  }

  public <T> T get(Key<T> key) {
    return _cast(map.get(key));
  }

  public <T> T getOrPut(Key<T> key, Function<Key<T>, T> defaultValue) {
    if (contains(key)) {
      return get(key);
    } else {
      val value = defaultValue.apply(key);
      put(key, value);
      return value;
    }
  }
}
