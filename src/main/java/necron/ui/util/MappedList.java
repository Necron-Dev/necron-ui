package necron.ui.util;

import java.util.AbstractList;
import java.util.List;
import java.util.function.Function;

public class MappedList {
  public static <T, E> List<E> create(
    List<? extends T> original,
    Function<? super T, ? extends E> transformer
  ) {
    return new AbstractList<>() {
      @Override
      public E get(int index) {
        return transformer.apply(original.get(index));
      }

      @Override
      public int size() {
        return original.size();
      }
    };
  }
}
