package necron.ui.react;

import lombok.Value;
import lombok.val;
import necron.ui.util.WeakClosable;

import java.util.LinkedHashSet;
import java.util.Set;

public class HookManager<T> {
  @Value
  private static class HookEntry<T> {
    long hookId;
    T hook;

    @Override
    public int hashCode() {
      return (int) hookId;
    }

    @Override
    public boolean equals(Object object) {
      return object instanceof HookEntry<?> entry && hookId == entry.hookId;
    }
  }

  private final Set<HookEntry<T>> hooks = new LinkedHashSet<>();
  private long currentHookId = 0;

  public AutoCloseable hook(T hook) {
    val hookId = currentHookId++;
    hooks.add(new HookEntry<>(hookId, hook));
    return new WeakClosable(() -> hooks.remove(new HookEntry<T>(hookId, null)));
  }

  public Iterable<T> getHooks() {
    return hooks.stream().map(HookEntry::getHook)::iterator;
  }
}
