package necron.ui.react;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public abstract class SerialReact<T> extends HookManager<HookFn<? super T>> implements React<T> {
  @Getter
  @Setter(AccessLevel.PROTECTED)
  private long serial = 0;

  protected void triggerHooks(T oldValue, T newValue) {
    for (val hook : getHooks()) {
      hook.onChange(oldValue, newValue);
    }
  }

  protected void markDirty() {
    serial++;
  }
}

