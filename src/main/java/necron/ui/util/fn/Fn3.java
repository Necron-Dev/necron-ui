package necron.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn3<A, B, C, R> extends Fn3v<A, B, C>, Fn4<A, B, C, Object, R> {
  R invoke(A a, B b, C c);

  @Override
  default void invokev(A a, B b, C c) {
    invoke(a, b, c);
  }

  @Override
  default void invokev(A a, B b, C c, Object object) {
    invoke(a, b, c);
  }

  @Override
  default R invoke(A a, B b, C c, Object object) {
    return invoke(a, b, c);
  }

  @Override
  default R call(Object... args) {
    check(args);
    return invoke(
      _cast(args[0]),
      _cast(args[1]),
      _cast(args[2])
    );
  }

  @Override
  default void callv(Object... args) {
    call(args);
  }

  static <A, B, C, R> Fn3<A, B, C, R> of(Fn3<? super A, ? super B, ? super C, ? extends R> fn) {
    return _cast(fn);
  }

  static <A, B, C, R> Fn3<A, B, C, R> fn(Fn3<? super A, ? super B, ? super C, ? extends R> fn) {
    return _cast(fn);
  }
}
