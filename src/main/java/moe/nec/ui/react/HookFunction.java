package moe.nec.ui.react;

public interface HookFunction<T> {
  void onChange(T oldValue, T newValue);
}
