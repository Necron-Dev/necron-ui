package necron.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn7v<A, B, C, D, E, F, G> extends Fn8v<A, B, C, D, E, F, G, Object> {
  void invokev(A a, B b, C c, D d, E e, F f, G g);

  @Override
  default void invokev(A a, B b, C c, D d, E e, F f, G g, Object object) {
    invokev(a, b, c, d, e, f, g);
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
      _cast(args[6])
    );
  }

  @Override
  default int params() {
    return 7;
  }

  static <A, B, C, D, E, F, G> Fn7v<A, B, C, D, E, F, G> of(Fn7v<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G> fn) {
    return _cast(fn);
  }

  static <A, B, C, D, E, F, G> Fn7v<A, B, C, D, E, F, G> fnv(Fn7v<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G> fn) {
    return _cast(fn);
  }
}
