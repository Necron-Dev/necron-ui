package moe.nec.ui.react;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class SerialReact<T> implements React<T> {
  private record HookEntry<T>(
    long hookId,
    HookFunction<T> hook
  ) {
    @Override
    public int hashCode() {
      return (int) hookId;
    }

    @Override
    public boolean equals(Object object) {
      return object instanceof HookEntry<?> entry && hookId == entry.hookId;
    }
  }

  private final Set<HookEntry<? super T>> hooks = new LinkedHashSet<>();
  private long currentHookId = 0;

  @Getter
  @Setter(AccessLevel.PROTECTED)
  private long serial = 0;

  @Override
  public Hook hook(HookFunction<? super T> hook) {
    val hookId = currentHookId++;
    hooks.add(new HookEntry<>(hookId, hook));
    return new Hook(this, hookId);
  }

  public void unhook(long hookId) {
    hooks.remove(new HookEntry<T>(hookId, null));
  }

  protected void triggerHooks(T oldValue, T newValue) {
    for (val hook : hooks) {
      hook.hook.onChange(oldValue, newValue);
    }
  }

  protected void markDirty() {
    serial++;
  }
}

