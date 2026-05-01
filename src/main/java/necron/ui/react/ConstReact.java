package necron.ui.react;

public class ConstReact<T> implements React<T> {
  private static final AutoCloseable DUMMY_HOOK = () -> {};

  private final T value;

  public ConstReact(T value) {
    this.value = value;
  }

  @Override
  public T get() {
    return value;
  }

  @Override
  public AutoCloseable hook(HookFn<? super T> hook) {
    return DUMMY_HOOK;
  }

  @Override
  public long getSerial() {
    return 0;
  }
}
