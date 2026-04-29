package moe.nec.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn6<A, B, C, D, E, F, R> extends Fn6v<A, B, C, D, E, F>, Fn7<A, B, C, D, E, F, Object, R> {
  R invoke(A a, B b, C c, D d, E e, F f);

  @Override
  default void invokev(A a, B b, C c, D d, E e, F f) {
    invoke(a, b, c, d, e, f);
  }

  @Override
  default void invokev(A a, B b, C c, D d, E e, F f, Object object) {
    invoke(a, b, c, d, e, f);
  }

  @Override
  default R invoke(A a, B b, C c, D d, E e, F f, Object object) {
    return invoke(a, b, c, d, e, f);
  }

  @Override
  default R call(Object... args) {
    check(args);
    return invoke(
      _cast(args[0]),
      _cast(args[1]),
      _cast(args[2]),
      _cast(args[3]),
      _cast(args[4]),
      _cast(args[5])
    );
  }

  @Override
  default void callv(Object... args) {
    call(args);
  }

  static <A, B, C, D, E, F, R> Fn6<A, B, C, D, E, F, R> of(Fn6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? extends R> fn) {
    return _cast(fn);
  }

  static <A, B, C, D, E, F, R> Fn6<A, B, C, D, E, F, R> fn(Fn6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? extends R> fn) {
    return _cast(fn);
  }
}
