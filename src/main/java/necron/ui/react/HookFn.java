package necron.ui.react;

@FunctionalInterface
public interface HookFn<T> {
  void onChange(T oldValue, T newValue);
}
