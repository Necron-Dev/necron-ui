package moe.nec.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn8v<A, B, C, D, E, F, G, H> extends Fn9v<A, B, C, D, E, F, G, H, Object> {
  void invokev(A a, B b, C c, D d, E e, F f, G g, H h);

  @Override
  default void invokev(A a, B b, C c, D d, E e, F f, G g, H h, Object object) {
    invokev(a, b, c, d, e, f, g, h);
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
      _cast(args[5]),
      _cast(args[6]),
      _cast(args[7])
    );
  }

  @Override
  default int params() {
    return 8;
  }

  static <A, B, C, D, E, F, G, H> Fn8v<A, B, C, D, E, F, G, H> of(Fn8v<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H> fn) {
    return _cast(fn);
  }

  static <A, B, C, D, E, F, G, H> Fn8v<A, B, C, D, E, F, G, H> fnv(Fn8v<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H> fn) {
    return _cast(fn);
  }
}
