package moe.nec.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn2<A, B, R> extends Fn2v<A, B>, Fn3<A, B, Object, R> {
  R invoke(A a, B b);

  @Override
  default void invokev(A a, B b) {
    invoke(a, b);
  }

  @Override
  default void invokev(A a, B b, Object object) {
    invoke(a, b);
  }

  @Override
  default R invoke(A a, B b, Object object) {
    return invoke(a, b);
  }

  @Override
  default R call(Object... args) {
    check(args);
    return invoke(
      _cast(args[0]),
      _cast(args[1])
    );
  }

  @Override
  default void callv(Object... args) {
    call(args);
  }

  static <A, B, R> Fn2<A, B, R> of(Fn2<? super A, ? super B, ? extends R> fn) {
    return _cast(fn);
  }

  static <A, B, R> Fn2<A, B, R> fn(Fn2<? super A, ? super B, ? extends R> fn) {
    return _cast(fn);
  }
}
