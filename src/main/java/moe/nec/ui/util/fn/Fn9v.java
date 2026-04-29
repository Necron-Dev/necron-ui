package moe.nec.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn9v<A, B, C, D, E, F, G, H, I> extends Fnv {
  void invokev(A a, B b, C c, D d, E e, F f, G g, H h, I i);

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
      _cast(args[7]),
      _cast(args[8])
    );
  }

  @Override
  default int params() {
    return 9;
  }

  static <A, B, C, D, E, F, G, H, I> Fn9v<A, B, C, D, E, F, G, H, I> of(Fn9v<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I> fn) {
    return _cast(fn);
  }

  static <A, B, C, D, E, F, G, H, I> Fn9v<A, B, C, D, E, F, G, H, I> fnv(Fn9v<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I> fn) {
    return _cast(fn);
  }
}
