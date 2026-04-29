package necron.ui.util.fn;

import lombok.val;

import java.util.function.Consumer;

public interface Fnv {
  void callv(Object... args);

  int params();

  default void check(Object... args) {
    val params = params();
    if (params > args.length) {
      throw new IllegalArgumentException(String.format(
        "expected at least %d parameter%s, given %d",
        params,
        params == 1 ? "" : "s",
        args.length
      ));
    }
  }

  static Fnv of(int params, Consumer<? super Object[]> fn) {
    return new Fnv() {
      @Override
      public void callv(Object... args) {
        fn.accept(args);
      }

      @Override
      public int params() {
        return params;
      }
    };
  }

  static Fnv of(Consumer<? super Object[]> fn) {
    return of(Integer.MAX_VALUE, fn);
  }

  static Fnv fnv(int params, Consumer<? super Object[]> fn) {
    return new Fnv() {
      @Override
      public void callv(Object... args) {
        fn.accept(args);
      }

      @Override
      public int params() {
        return params;
      }
    };
  }
}
