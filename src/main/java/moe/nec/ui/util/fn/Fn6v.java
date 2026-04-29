package moe.nec.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn6v<A, B, C, D, E, F> extends Fn7v<A, B, C, D, E, F, Object> {
  void invokev(A a, B b, C c, D d, E e, F f);

  @Override
  default void invokev(A a, B b, C c, D d, E e, F f, Object object) {
    invokev(a, b, c, d, e, f);
  }

  @Override
  default void callv(Object... args) {
    check(args);
    invokev(
      _cast(args[0]),
      _cast(args[1]),
      _cast(args[2]),
      _cast(args[3]),
      _cast(args[4]),
      _cast(args[5])
    );
  }

  @Override
  default int params() {
    return 6;
  }

  static <A, B, C, D, E, F> Fn6v<A, B, C, D, E, F> of(Fn6v<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> fn) {
    return _cast(fn);
  }

  static <A, B, C, D, E, F> Fn6v<A, B, C, D, E, F> fnv(Fn6v<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> fn) {
    return _cast(fn);
  }
}
