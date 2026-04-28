package moe.nec.ui.react;

public class ConstReact<T> implements React<T> {
  private final T value;
  private final AutoCloseable dummyHook = () -> {};

  public ConstReact(T value) {
    this.value = value;
  }

  @Override
  public T get() {
    return value;
  }

  @Override
  public AutoCloseable hook(HookFunction<? super T> hook) {
    return dummyHook;
  }

  @Override
  public long getSerial() {
    return 0;
  }
}
