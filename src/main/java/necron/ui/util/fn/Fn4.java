package necron.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn4<A, B, C, D, R> extends Fn4v<A, B, C, D>, Fn5<A, B, C, D, Object, R> {
  R invoke(A a, B b, C c, D d);

  @Override
  default void invokev(A a, B b, C c, D d) {
    invoke(a, b, c, d);
  }

  @Override
  default void invokev(A a, B b, C c, D d, Object object) {
    invoke(a, b, c, d);
  }

  @Override
  default R invoke(A a, B b, C c, D d, Object object) {
    return invoke(a, b, c, d);
  }

  @Override
  default R call(Object... args) {
    check(args);
    return invoke(
      _cast(args[0]),
      _cast(args[1]),
      _cast(args[2]),
      _cast(args[3])
    );
  }

  @Override
  default void callv(Object... args) {
    call(args);
  }

  static <A, B, C, D, R> Fn4<A, B, C, D, R> of(Fn4<? super A, ? super B, ? super C, ? super D, ? extends R> fn) {
    return _cast(fn);
  }

  static <A, B, C, D, R> Fn4<A, B, C, D, R> fn(Fn4<? super A, ? super B, ? super C, ? super D, ? extends R> fn) {
    return _cast(fn);
  }
}
