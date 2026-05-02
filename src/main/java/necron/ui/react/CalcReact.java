package necron.ui.react;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import necron.ui.util.SmartClosable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiPredicate;

import static yqloss.E._cast;

public abstract class CalcReact<T> extends SerialReact<T> implements SmartClosable {
  @Data
  @AllArgsConstructor
  private static class Dependency {
    public final React<?> react;
    public long serial;
  }

  private final HookFn<?> hookFn = (_, _) -> forceUpdate();
  private final List<AutoCloseable> hooks = new ArrayList<>();
  private final List<Dependency> dependencies = new ArrayList<>();
  private final BiPredicate<? super T, ? super T> equals;
  private T value;

  protected CalcReact(BiPredicate<? super T, ? super T> equals) {
    this.equals = equals;
    value = calculate();
  }

  protected CalcReact(BiPredicate<? super T, ? super T> equals, T initial) {
    this.equals = equals;
    value = initial;
  }

  @Override
  public T get() {
    update();
    return value;
  }

  @Override
  public T peek() {
    return value;
  }

  public CalcReact<T> dependsOn(React<?>... reacts) {
    for (val react : reacts) {
      dependencies.add(new Dependency(react, react.getSerial()));
    }
    return this;
  }

  public CalcReact<T> cancelDependencies(React<?>... reacts) {
    val reactSet = Set.of(reacts);
    dependencies.removeIf(x -> reactSet.contains(x.react));
    return this;
  }

  public final CalcReact<T> listensTo(React<?>... reacts) {
    for (val react : reacts) {
      hooks.add(react.hook(_cast(hookFn)));
    }
    return this;
  }

  public void forceUpdate() {
    for (val dependency : dependencies) {
      dependency.react.get();
    }
    val oldValue = value;
    val newValue = calculate();
    if (equals != null && equals.test(oldValue, newValue)) {
      return;
    }
    markDirty();
    value = newValue;
    triggerHooks(oldValue, newValue);
  }

  public void update() {
    var doUpdate = false;
    for (val dependency : dependencies) {
      dependency.react.get();
      val serial = dependency.react.getSerial();
      if (serial != dependency.serial) {
        dependency.serial = serial;
        doUpdate = true;
      }
    }
    if (doUpdate) {
      forceUpdate();
    }
  }

  protected abstract T calculate();

  @Override
  public void collectObjectsToClose(Queue<? super Object> queue) {
    queue.addAll(hooks);
  }
}
