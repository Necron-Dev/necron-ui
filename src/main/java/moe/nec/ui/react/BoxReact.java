package moe.nec.ui.react;

import lombok.AllArgsConstructor;
import lombok.val;

import java.util.Objects;

@AllArgsConstructor
public class BoxReact<T> extends SerialReact<T> {
  private final boolean checkForEquals;
  private T value;

  @Override
  public T get() {
    return value;
  }

  public void set(T newValue) {
    val oldValue = value;
    if (checkForEquals && Objects.equals(oldValue, newValue)) {
      return;
    }
    markDirty();
    value = newValue;
    triggerHooks(oldValue, newValue);
  }
}
