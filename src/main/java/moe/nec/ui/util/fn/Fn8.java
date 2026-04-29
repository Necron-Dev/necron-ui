package moe.nec.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn8<A, B, C, D, E, F, G, H, R> extends Fn8v<A, B, C, D, E, F, G, H>, Fn9<A, B, C, D, E, F, G, H, Object, R> {
  R invoke(A a, B b, C c, D d, E e, F f, G g, H h);

  @Override
  default void invokev(A a, B b, C c, D d, E e, F f, G g, H h) {
    invoke(a, b, c, d, e, f, g, h);
  }

  @Override
  default void invokev(A a, B b, C c, D d, E e, F f, G g, H h, Object object) {
    invoke(a, b, c, d, e, f, g, h);
  }

  @Override
  default R invoke(A a, B b, C c, D d, E e, F f, G g, H h, Object object) {
    return invoke(a, b, c, d, e, f, g, h);
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
      _cast(args[6]),
      _cast(args[7])
    );
  }

  @Override
  default void callv(Object... args) {
    call(args);
  }

  static <A, B, C, D, E, F, G, H, R> Fn8<A, B, C, D, E, F, G, H, R> of(Fn8<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? extends R> fn) {
    return _cast(fn);
  }

  static <A, B, C, D, E, F, G, H, R> Fn8<A, B, C, D, E, F, G, H, R> fn(Fn8<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? extends R> fn) {
    return _cast(fn);
  }
}
