package necron.ui.react;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import necron.ui.util.SmartClosable;

import java.util.*;

import static yqloss.E._cast;

public abstract class CalcReact<T> extends SerialReact<T> implements SmartClosable {
  @Data
  @AllArgsConstructor
  private static class Dependency {
    public final React<?> react;
    public long serial;
  }

  private final HookFunction<?> hookFunction = (_, _) -> forceUpdate();
  private final List<AutoCloseable> hooks = new ArrayList<>();
  private final List<Dependency> dependencies = new ArrayList<>();
  private final boolean checkForEquals;
  private T value;

  protected CalcReact(boolean checkForEquals) {
    this.checkForEquals = checkForEquals;
    value = calculate();
  }

  protected CalcReact(boolean checkForEquals, T initial) {
    this.checkForEquals = checkForEquals;
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
      hooks.add(react.hook(_cast(hookFunction)));
    }
    return this;
  }

  public void forceUpdate() {
    val oldValue = value;
    val newValue = calculate();
    if (checkForEquals && Objects.equals(oldValue, newValue)) {
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
