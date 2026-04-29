package necron.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn5<A, B, C, D, E, R> extends Fn5v<A, B, C, D, E>, Fn6<A, B, C, D, E, Object, R> {
  R invoke(A a, B b, C c, D d, E e);

  @Override
  default void invokev(A a, B b, C c, D d, E e) {
    invoke(a, b, c, d, e);
  }

  @Override
  default void invokev(A a, B b, C c, D d, E e, Object object) {
    invoke(a, b, c, d, e);
  }

  @Override
  default R invoke(A a, B b, C c, D d, E e, Object object) {
    return invoke(a, b, c, d, e);
  }

  @Override
  default R call(Object... args) {
    check(args);
    return invoke(
      _cast(args[0]),
      _cast(args[1]),
      _cast(args[2]),
      _cast(args[3]),
      _cast(args[4])
    );
  }

  @Override
  default void callv(Object... args) {
    call(args);
  }

  static <A, B, C, D, E, R> Fn5<A, B, C, D, E, R> of(Fn5<? super A, ? super B, ? super C, ? super D, ? super E, ? extends R> fn) {
    return _cast(fn);
  }

  static <A, B, C, D, E, R> Fn5<A, B, C, D, E, R> fn(Fn5<? super A, ? super B, ? super C, ? super D, ? super E, ? extends R> fn) {
    return _cast(fn);
  }
}
