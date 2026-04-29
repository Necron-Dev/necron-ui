package moe.nec.ui.react;

import lombok.val;
import moe.nec.ui.util.fn.Fn;

import java.util.function.Supplier;

public interface React<T> extends Supplier<T> {
  AutoCloseable hook(HookFunction<? super T> hook);

  default React<T> hooking(HookFunction<? super T> hook) {
    hook(hook);
    return this;
  }

  default T peek() {
    return get();
  }

  long getSerial();

  static <T> React<T> of(T value) {
    return new ConstReact<>(value);
  }

  static <T> React<T> constant(T value) {
    return of(value);
  }

  static <T> SerialReact<T> react(Supplier<? extends T> value, React<?>... dependencies) {
    return new FnReact<T>(true, value).dependsOn(dependencies);
  }

  static <R> SerialReact<R> react(Fn<? extends R> value, React<?>... dependencies) {
    return react(
      () -> {
        val values = new Object[dependencies.length];
        for (var i = 0; i < values.length; i++) {
          values[i] = dependencies[i].peek();
        }
        return value.call(values);
      }, dependencies
    );
  }

  static <T> SerialReact<T> listen(Supplier<? extends T> value, React<?>... dependencies) {
    return new FnReact<T>(true, value).dependsOn(dependencies);
  }

  static <R> SerialReact<R> listen(Fn<? extends R> value, React<?>... dependencies) {
    return react(
      () -> {
        val values = new Object[dependencies.length];
        for (var i = 0; i < values.length; i++) {
          values[i] = dependencies[i].peek();
        }
        return value.call(values);
      }, dependencies
    );
  }
}
