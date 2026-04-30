package necron.ui.react;

import lombok.AllArgsConstructor;
import lombok.val;

import java.util.function.BiPredicate;

@AllArgsConstructor
public class BoxReact<T> extends SerialReact<T> {
  private final BiPredicate<? super T, ? super T> equals;
  private T value;

  @Override
  public T get() {
    return value;
  }

  public void set(T newValue) {
    val oldValue = value;
    if (equals != null && equals.test(oldValue, newValue)) {
      return;
    }
    markDirty();
    value = newValue;
    triggerHooks(oldValue, newValue);
  }
}
