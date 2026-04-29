package moe.nec.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn2v<A, B> extends Fn3v<A, B, Object> {
  void invokev(A a, B b);

  @Override
  default void invokev(A a, B b, Object object) {
    invokev(a, b);
  }

  @Override
  default void callv(Object... args) {
    check(args);
    invokev(
      _cast(args[0]),
      _cast(args[1])
    );
  }

  @Override
  default int params() {
    return 2;
  }

  static <A, B> Fn2v<A, B> of(Fn2v<? super A, ? super B> fn) {
    return _cast(fn);
  }

  static <A, B> Fn2v<A, B> fnv(Fn2v<? super A, ? super B> fn) {
    return _cast(fn);
  }
}
