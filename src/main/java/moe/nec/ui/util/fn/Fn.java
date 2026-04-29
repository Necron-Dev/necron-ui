package moe.nec.ui.util.fn;

import java.util.function.Function;

public interface Fn<R> extends Fnv {
  R call(Object... args);

  @Override
  default void callv(Object... args) {
    call(args);
  }

  static <R> Fn<R> of(int params, Function<? super Object[], ? extends R> fn) {
    return new Fn<>() {
      @Override
      public R call(Object... args) {
        return fn.apply(args);
      }

      @Override
      public int params() {
        return params;
      }
    };
  }

  static <R> Fn<R> of(Function<? super Object[], ? extends R> fn) {
    return of(Integer.MAX_VALUE, fn);
  }

  static <R> Fn<R> fn(int params, Function<? super Object[], ? extends R> fn) {
    return of(params, fn);
  }
}
