package necron.ui.react;

import lombok.SneakyThrows;
import lombok.val;
import necron.ui.util.SmartClosable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

import static yqloss.E.$;

public class SubListReact<T> extends SerialReact<List<T>> implements SmartClosable, ListReact<T> {
  private final List<T> list = new ArrayList<>();
  private React<?> parent;
  private AutoCloseable moveHook, insertHook, removeHook;

  private final HookManager<MoveFn<? super T>> moveHooks = new HookManager<>();
  private final HookManager<InsertFn<? super T>> insertHooks = new HookManager<>();
  private final HookManager<RemoveFn<? super T>> removeHooks = new HookManager<>();

  @SneakyThrows
  public <V> SubListReact<T> setParent(
    ListReact<? extends V> parent,
    Function<? super V, ? extends T> transformer
  ) {
    if (!list.isEmpty()) {
      throw new UnsupportedOperationException("cannot set parent of SubListReact when not empty");
    }
    if (moveHook != null) moveHook.close();
    if (insertHook != null) insertHook.close();
    if (removeHook != null) removeHook.close();
    this.parent = parent;
    if (parent != null) {
      for (val value : parent.peek()) {
        val transformed = transformer.apply(value);
        list.add(transformed);
        for (val hook : insertHooks.getHooks()) {
          hook.onInsert(list.size() - 1, transformed);
        }
      }
    }
    moveHook = $(parent.hookMove((from, to, _) -> {
      val moved = list.remove(from);
      list.add(to, moved);
      markDirty();
      for (val hook : moveHooks.getHooks()) {
        hook.onMove(from, to, moved);
      }
    }));
    insertHook = $(parent.hookInsert((index, value) -> {
      val inserted = transformer.apply(value);
      list.add(index, inserted);
      markDirty();
      for (val hook : insertHooks.getHooks()) {
        hook.onInsert(index, inserted);
      }
    }));
    removeHook = $(parent.hookRemove((index, _) -> {
      val removed = list.remove(index);
      markDirty();
      for (val hook : removeHooks.getHooks()) {
        hook.onRemove(index, removed);
      }
    }));
    markDirty();
    return this;
  }

  @Override
  public List<T> get() {
    $(parent.get());
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
