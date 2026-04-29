package moe.nec.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn4v<A, B, C, D> extends Fn5v<A, B, C, D, Object> {
  void invokev(A a, B b, C c, D d);

  @Override
  default void invokev(A a, B b, C c, D d, Object object) {
    invokev(a, b, c, d);
  }

  @Override
  default void callv(Object... args) {
    check(args);
    invokev(
      _cast(args[0]),
      _cast(args[1]),
      _cast(args[2]),
      _cast(args[3])
    );
  }

  @Override
  default int params() {
    return 4;
  }

  static <A, B, C, D> Fn4v<A, B, C, D> of(Fn4v<? super A, ? super B, ? super C, ? super D> fn) {
    return _cast(fn);
  }

  static <A, B, C, D> Fn4v<A, B, C, D> fnv(Fn4v<? super A, ? super B, ? super C, ? super D> fn) {
    return _cast(fn);
  }
}
