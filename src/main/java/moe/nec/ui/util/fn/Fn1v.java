package moe.nec.ui.util.fn;

import java.util.function.Consumer;

import static yqloss.E._cast;

@FunctionalInterface
public interface Fn1v<A> extends Fn2v<A, Object>, Consumer<A> {
  void invokev(A a);

  @Override
  default void invokev(A a, Object object) {
    invokev(a);
  }

  @Override
  default void callv(Object... args) {
    check(args);
    invokev(
      _cast(args[0])
    );
  }

  @Override
  default int params() {
    return 1;
  }

  @Override
  default void accept(A a) {
    invokev(a);
  }

  static <A> Fn1v<A> of(Fn1v<? super A> fn) {
    return _cast(fn);
  }

  static <A> Fn1v<A> fnv(Fn1v<? super A> fn) {
    return _cast(fn);
  }
}
