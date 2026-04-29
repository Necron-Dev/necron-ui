package necron.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn9<A, B, C, D, E, F, G, H, I, R> extends Fn<R>, Fn9v<A, B, C, D, E, F, G, H, I> {
  R invoke(A a, B b, C c, D d, E e, F f, G g, H h, I i);

  @Override
  default void invokev(A a, B b, C c, D d, E e, F f, G g, H h, I i) {
    invoke(a, b, c, d, e, f, g, h, i);
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
      _cast(args[7]),
      _cast(args[8])
    );
  }

  @Override
  default void callv(Object... args) {
    call(args);
  }

  static <A, B, C, D, E, F, G, H, I, R> Fn9<A, B, C, D, E, F, G, H, I, R> of(Fn9<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I, ? extends R> fn) {
    return _cast(fn);
  }

  static <A, B, C, D, E, F, G, H, I, R> Fn9<A, B, C, D, E, F, G, H, I, R> fn(Fn9<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I, ? extends R> fn) {
    return _cast(fn);
  }
}
