package moe.nec.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn7<A, B, C, D, E, F, G, R> extends Fn7v<A, B, C, D, E, F, G>, Fn8<A, B, C, D, E, F, G, Object, R> {
  R invoke(A a, B b, C c, D d, E e, F f, G g);

  @Override
  default void invokev(A a, B b, C c, D d, E e, F f, G g) {
    invoke(a, b, c, d, e, f, g);
  }

  @Override
  default void invokev(A a, B b, C c, D d, E e, F f, G g, Object object) {
    invoke(a, b, c, d, e, f, g);
  }

  @Override
  default R invoke(A a, B b, C c, D d, E e, F f, G g, Object object) {
    return invoke(a, b, c, d, e, f, g);
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
      _cast(args[5]),
      _cast(args[6])
    );
  }

  @Override
  default void callv(Object... args) {
    call(args);
  }

  static <A, B, C, D, E, F, G, R> Fn7<A, B, C, D, E, F, G, R> of(Fn7<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? extends R> fn) {
    return _cast(fn);
  }

  static <A, B, C, D, E, F, G, R> Fn7<A, B, C, D, E, F, G, R> fn(Fn7<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? extends R> fn) {
    return _cast(fn);
  }
}
