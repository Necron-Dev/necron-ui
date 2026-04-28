package moe.nec.ui.react;

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

  static <T> SerialReact<T> from(Supplier<? extends T> value, React<?>... dependencies) {
    return new FnReact<T>(true, value).dependsOn(dependencies);
  }

  static <T> SerialReact<T> listen(Supplier<? extends T> value, React<?>... dependencies) {
    return new FnReact<T>(true, value).dependsOn(dependencies);
  }
}
