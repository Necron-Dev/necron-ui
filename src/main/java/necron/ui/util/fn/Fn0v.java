package necron.ui.util.fn;

@FunctionalInterface
public interface Fn0v extends Fn1v<Object>, Runnable {
  void invokev();

  @Override
  default void invokev(Object object) {
    invokev();
  }

  @Override
  default void callv(Object... args) {
    check(args);
    invokev();
  }

  @Override
  default int params() {
    return 0;
  }

  @Override
  default void run() {
    invokev();
  }

  static Fn0v of(Fn0v fn) {
    return fn;
  }

  static Fn0v fnv(Fn0v fn) {
    return fn;
  }
}
