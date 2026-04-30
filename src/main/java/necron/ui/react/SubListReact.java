package necron.ui.react;

import lombok.val;
import necron.ui.util.SmartClosable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

public class SubListReact<T> extends SerialReact<List<T>> implements SmartClosable, ListReact<T> {
  private final List<T> list;
  private final AutoCloseable moveHook, insertHook, removeHook;

  private final HookManager<MoveFn<? super T>> moveHooks = new HookManager<>();
  private final HookManager<InsertFn<? super T>> insertHooks = new HookManager<>();
  private final HookManager<RemoveFn<? super T>> removeHooks = new HookManager<>();

  public <V> SubListReact(ListReact<? extends V> parent, Function<? super V, ? extends T> transformer) {
    list = new ArrayList<>(parent.peek().stream().map(transformer).toList());
    moveHook = parent.hookMove((from, to, _) -> {
      val moved = list.remove(from);
      list.add(to, moved);
      markDirty();
      for (val hook : moveHooks.getHooks()) {
        hook.onMove(from, to, moved);
      }
    });
    insertHook = parent.hookInsert((index, value) -> {
      val inserted = transformer.apply(value);
      list.add(index, inserted);
      markDirty();
      for (val hook : insertHooks.getHooks()) {
        hook.onInsert(index, inserted);
      }
    });
    removeHook = parent.hookRemove((index, _) -> {
      val removed = list.remove(index);
      markDirty();
      for (val hook : removeHooks.getHooks()) {
        hook.onRemove(index, removed);
      }
    });
  }

  @Override
  public List<T> get() {
    return list;
  }

  @Override
  public void collectObjectsToClose(Queue<? super Object> queue) {
    queue.add(moveHook);
    queue.add(insertHook);
    queue.add(removeHook);
  }

  @Override
  public AutoCloseable hookMove(MoveFn<? super T> hook) {
    return moveHooks.hook(hook);
  }

  @Override
  public AutoCloseable hookInsert(InsertFn<? super T> hook) {
    return insertHooks.hook(hook);
  }

  @Override
  public AutoCloseable hookRemove(RemoveFn<? super T> hook) {
    return removeHooks.hook(hook);
  }
}
