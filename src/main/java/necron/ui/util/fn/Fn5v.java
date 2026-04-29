package necron.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn5v<A, B, C, D, E> extends Fn6v<A, B, C, D, E, Object> {
  void invokev(A a, B b, C c, D d, E e);

  @Override
  default void invokev(A a, B b, C c, D d, E e, Object object) {
    invokev(a, b, c, d, e);
  }

  @Override
  default void callv(Object... args) {
    check(args);
    invokev(
      _cast(args[0]),
      _cast(args[1]),
      _cast(args[2]),
      _cast(args[3]),
      _cast(args[4])
    );
  }

  @Override
  default int params() {
    return 5;
  }

  static <A, B, C, D, E> Fn5v<A, B, C, D, E> of(Fn5v<? super A, ? super B, ? super C, ? super D, ? super E> fn) {
    return _cast(fn);
  }

  static <A, B, C, D, E> Fn5v<A, B, C, D, E> fnv(Fn5v<? super A, ? super B, ? super C, ? super D, ? super E> fn) {
    return _cast(fn);
  }
}
