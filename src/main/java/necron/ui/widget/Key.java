package necron.ui.widget;

import lombok.val;
import necron.ui.react.ConstructorWithKey;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public class Key {
  private final Object[] objects;

  public Key(Object... objects) {
    this.objects = objects;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Key key && Objects.deepEquals(objects, key.objects);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(objects);
  }

  @Override
  public String toString() {
    return Arrays.toString(objects);
  }

  public static Key key(Object... objects) {
    return new Key(objects);
  }

  public static Key of(Object... objects) {
    return new Key(objects);
  }

  public static <T> ConstructorWithKey<T> constructor(
    Function<? super Object, ? extends T> constructor,
    Object... objects
  ) {
    val key = of(objects);
    return new ConstructorWithKey<>() {
      @Override
      public T construct() {
        return constructor.apply(key);
      }

      @Override
      public Object getKey() {
        return key;
      }
    };
  }
}
