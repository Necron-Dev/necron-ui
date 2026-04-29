package moe.nec.ui.util.fn;

import java.util.function.Function;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn1<A, R> extends Fn1v<A>, Fn2<A, Object, R>, Function<A, R> {
  R invoke(A a);

  @Override
  default void invokev(A a) {
    invoke(a);
  }

  @Override
  default void invokev(A a, Object object) {
    invoke(a);
  }

  @Override
  default R invoke(A a, Object object) {
    return invoke(a);
  }

  @Override
  default R apply(A a) {
    return invoke(a);
  }

  @Override
  default R call(Object... args) {
    check(args);
    return invoke(
      _cast(args[0])
    );
  }

  @Override
  default void callv(Object... args) {
    call(args);
  }

  static <A, R> Fn1<A, R> of(Fn1<? super A, ? extends R> fn) {
    return _cast(fn);
  }

  static <A, R> Fn1<A, R> fn(Fn1<? super A, ? extends R> fn) {
    return _cast(fn);
  }
}
