package moe.nec.ui.util.fn;

import java.util.function.Supplier;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn0<R> extends Fn0v, Fn1<Object, R>, Supplier<R> {
  R invoke();

  @Override
  default void invokev() {
    invoke();
  }

  @Override
  default void invokev(Object object) {
    invoke();
  }

  @Override
  default R invoke(Object object) {
    return invoke();
  }

  @Override
  default R get() {
    return invoke();
  }

  @Override
  default R call(Object... args) {
    check(args);
    return invoke();
  }

  @Override
  default void callv(Object... args) {
    call(args);
  }

  static <R> Fn0<R> of(Fn0<? extends R> fn) {
    return _cast(fn);
  }

  static <R> Fn0<R> fn(Fn0<? extends R> fn) {
    return _cast(fn);
  }
}
