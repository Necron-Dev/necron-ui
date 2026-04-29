package necron.ui.util.fn;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn3v<A, B, C> extends Fn4v<A, B, C, Object> {
  void invokev(A a, B b, C c);

  @Override
  default void invokev(A a, B b, C c, Object object) {
    invokev(a, b, c);
  }

  @Override
  default void callv(Object... args) {
    check(args);
    invokev(
      _cast(args[0]),
      _cast(args[1]),
      _cast(args[2])
    );
  }

  @Override
  default int params() {
    return 3;
  }

  static <A, B, C> Fn3v<A, B, C> of(Fn3v<? super A, ? super B, ? super C> fn) {
    return _cast(fn);
  }

  static <A, B, C> Fn3v<A, B, C> fnv(Fn3v<? super A, ? super B, ? super C> fn) {
    return _cast(fn);
  }
}
