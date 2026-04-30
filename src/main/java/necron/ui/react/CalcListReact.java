package necron.ui.react;

import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static yqloss.E.$;

public abstract class CalcListReact<T extends WithKey> extends CalcReact<List<T>> implements ListReact<T> {
  private final HookManager<MoveFn<? super T>> moveHooks;
  private final HookManager<InsertFn<? super T>> insertHooks;
  private final HookManager<RemoveFn<? super T>> removeHooks;

  protected CalcListReact() {
    moveHooks = new HookManager<>();
    insertHooks = new HookManager<>();
    removeHooks = new HookManager<>();
    super((a, b) -> {
      if (a.size() != b.size()) return false;
      val iterA = a.iterator();
      val iterB = b.iterator();
      while (iterA.hasNext() && iterB.hasNext()) {
        if (!Objects.equals(iterA.next().getKey(), iterB.next().getKey())) {
          return false;
        }
      }
      return true;
    });
  }

  @Override
  protected List<T> calculate() {
    val result = calculateList();
    val self = new ArrayList<>($(peek(), List.of()));
    {
      val resultKeyMap = new HashMap<Object, Integer>();
      for (var i = 0; i < result.size; i++) {
        if (resultKeyMap.put(result.key.apply(i), i) != null) {
          throw new IllegalArgumentException("duplicate keys");
        }
      }
      for (var i = self.size() - 1; i >= 0; i--) {
        if (!resultKeyMap.containsKey(self.get(i).getKey())) {
          onRemove(i, self.remove(i));
        }
      }
    }
    {
      val selfKeyMap = new HashMap<Object, Integer>();
      for (var i = 0; i < self.size(); i++) {
        selfKeyMap.put(self.get(i).getKey(), i);
      }
      for (int i = 0, j = 0; i < result.size; i++) {
        val key = result.key.apply(i);
        val selfIndex = selfKeyMap.get(key);
        if (selfIndex != null) {
          val order = j++;
          if (selfIndex == order) continue;
          val removed = self.remove((int) selfIndex);
          self.add(order, removed);
          onMove(selfIndex, order, removed);
          for (var k = order; k <= selfIndex; k++) {
            selfKeyMap.put(self.get(k).getKey(), k);
          }
        }
      }
    }
    {
      val selfKeyMap = new HashMap<Object, Integer>();
      for (var i = 0; i < self.size(); i++) {
        if (selfKeyMap.put(self.get(i).getKey(), i) != null) {
          throw new IllegalArgumentException("duplicate keys");
        }
      }
      var insertIndex = self.size();
      for (var i = result.getSize() - 1; i >= 0; i--) {
        val key = result.key.apply(i);
        val selfIndex = selfKeyMap.get(key);
        if (selfIndex != null) {
          insertIndex = selfIndex;
          continue;
        }
        val element = result.value.apply(i);
        self.add(insertIndex, element);
        onInsert(insertIndex, element);
      }
    }
    return self;
  }

  @Value
  protected class CalculateListResult {
    int size;
    Function<? super Integer, ?> key;
    Function<? super Integer, ? extends T> value;
  }

  protected abstract CalculateListResult calculateList();

  protected void onMove(int from, int to, T element) {
    for (val hook : moveHooks.getHooks()) {
      hook.onMove(from, to, element);
    }
  }

  protected void onInsert(int index, T element) {
    for (val hook : insertHooks.getHooks()) {
      hook.onInsert(index, element);
    }
  }

  protected void onRemove(int index, T element) {
    for (val hook : removeHooks.getHooks()) {
      hook.onRemove(index, element);
    }
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
