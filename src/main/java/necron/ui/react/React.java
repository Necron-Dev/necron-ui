package necron.ui.react;

import lombok.val;
import necron.ui.util.MappedList;
import necron.ui.util.fn.Fn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface React<T> extends Supplier<T> {
  AutoCloseable hook(HookFn<? super T> hook);

  default React<T> hooking(HookFn<? super T> hook) {
    hook(hook);
    return this;
  }

  default T peek() {
    return get();
  }

  long getSerial();

  static UpdaterReact updater() {
    return new UpdaterReact();
  }

  static <T> ConstReact<T> of(T value) {
    return new ConstReact<>(value);
  }

  static <T> ConstReact<T> constant(T value) {
    return of(value);
  }

  static <T> BoxReact<T> box(BiPredicate<? super T, ? super T> equals, T value) {
    return new BoxReact<>(equals, value);
  }

  static <T> BoxReact<T> box(T value) {
    return box(Objects::equals, value);
  }

  static <T> CalcReact<T> react(
    BiPredicate<? super T, ? super T> equals,
    Supplier<? extends T> value,
    React<?>... dependencies
  ) {
    return new CalcReact<T>(equals) {
      @Override
      protected T calculate() {
        return value.get();
      }
    }.dependsOn(dependencies);
  }

  static <A, T> CalcReact<T> react(
    BiPredicate<? super T, ? super T> equals,
    React<? extends A> dependency,
    Function<? super A, ? extends T> value
  ) {
    return new CalcReact<T>(equals) {
      @Override
      protected T calculate() {
        return value.apply(dependency.peek());
      }
    }.dependsOn(dependency);
  }

  static <T> CalcReact<T> react(
    BiPredicate<? super T, ? super T> equals,
    Fn<? extends T> value,
    React<?>... dependencies
  ) {
    return react(
      equals,
      () -> {
        val values = new Object[dependencies.length];
        for (var i = 0; i < values.length; i++) {
          values[i] = dependencies[i].peek();
        }
        return value.call(values);
      }, dependencies
    );
  }

  static <A, T> CalcReact<T> react(
    React<? extends A> dependency,
    Function<? super A, ? extends T> value
  ) {
    return react(Objects::equals, dependency, value);
  }

  static <T> CalcReact<T> react(
    Supplier<? extends T> value,
    React<?>... dependencies
  ) {
    return react(Objects::equals, value, dependencies);
  }

  static <T> CalcReact<T> react(
    Fn<? extends T> value,
    React<?>... dependencies
  ) {
    return react(Objects::equals, value, dependencies);
  }

  static <T> CalcReact<T> listen(
    BiPredicate<? super T, ? super T> equals,
    Supplier<? extends T> value,
    React<?>... dependencies
  ) {
    return new CalcReact<T>(equals) {
      @Override
      protected T calculate() {
        return value.get();
      }
    }.listensTo(dependencies);
  }

  static <A, T> CalcReact<T> listen(
    BiPredicate<? super T, ? super T> equals,
    React<? extends A> dependency,
    Function<? super A, ? extends T> value
  ) {
    return new CalcReact<T>(equals) {
      @Override
      protected T calculate() {
        return value.apply(dependency.peek());
      }
    }.listensTo(dependency);
  }

  static <T> CalcReact<T> listen(
    BiPredicate<? super T, ? super T> equals,
    Fn<? extends T> value,
    React<?>... dependencies
  ) {
    return listen(
      equals,
      () -> {
        val values = new Object[dependencies.length];
        for (var i = 0; i < values.length; i++) {
          values[i] = dependencies[i].peek();
        }
        return value.call(values);
      }, dependencies
    );
  }

  static <T> CalcReact<T> listen(
    Supplier<? extends T> value,
    React<?>... dependencies
  ) {
    return listen(Objects::equals, value, dependencies);
  }

  static <A, T> CalcReact<T> listen(
    React<? extends A> dependency,
    Function<? super A, ? extends T> value
  ) {
    return listen(Objects::equals, dependency, value);
  }

  static <T> CalcReact<T> listen(
    Fn<? extends T> value,
    React<?>... dependencies
  ) {
    return listen(Objects::equals, value, dependencies);
  }

  static <T> SubListReact<T> subList() {
    return new SubListReact<>();
  }

  static <V, T> SubListReact<T> subList(
    ListReact<? extends V> parent,
    Function<? super V, ? extends T> transformer
  ) {
    return React.<T>subList().setParent(parent, transformer);
  }

  static <V, T> ListReact<T> mapList(
    ListReact<? extends V> parent,
    Function<? super V, ? extends T> transformer
  ) {
    return new ListReact<>() {
      @Override
      public AutoCloseable hookMove(MoveFn<? super T> hook) {
        return parent.hookMove((from, to, value) -> hook.onMove(from, to, transformer.apply(value)));
      }

      @Override
      public AutoCloseable hookInsert(InsertFn<? super T> hook) {
        return parent.hookInsert((index, value) -> hook.onInsert(index, transformer.apply(value)));
      }

      @Override
      public AutoCloseable hookRemove(RemoveFn<? super T> hook) {
        return parent.hookRemove((index, value) -> hook.onRemove(index, transformer.apply(value)));
      }

      @Override
      public AutoCloseable hook(HookFn<? super List<T>> hook) {
        return parent.hook((oldValue, newValue) -> hook.onChange(mapped(oldValue), mapped(newValue)));
      }

      @Override
      public long getSerial() {
        return parent.getSerial();
      }

      @Override
      public List<T> get() {
        return mapped(parent.get());
      }

      @Override
      public List<T> peek() {
        return mapped(parent.peek());
      }

      private List<T> mapped(List<? extends V> original) {
        return MappedList.create(original, transformer);
      }
    };
  }

  static <T extends WithKey> CalcListReact<T> calcList(
    Consumer<? super Consumer<? super ConstructorWithKey<? extends T>>> calc
  ) {
    return new CalcListReact<>() {
      @Override
      protected CalculateListResult calculateList() {
        val list = new ArrayList<ConstructorWithKey<? extends T>>();
        calc.accept((Consumer<? super ConstructorWithKey<? extends T>>) list::add);
        return new CalculateListResult(
          list.size(),
          i -> list.get(i).getKey(),
          i -> list.get(i).construct()
        );
      }
    };
  }

  static <T> CalcReact<T> listDepend(
    CalcReact<T> calcReact,
    ListReact<? extends React<?>> list
  ) {
    list.peek().forEach(calcReact::dependsOn);
    list
      .hookingInsert((_, react) -> calcReact.dependsOn(react))
      .hookingRemove((_, react) -> calcReact.cancelDependencies(react));
    return calcReact;
  }

  static <T> CalcReact<T> listListen(
    CalcReact<T> calcReact,
    ListReact<? extends React<?>> list
  ) {
    list.peek().forEach(calcReact::dependsOn);
    list.hookingInsert((_, react) -> calcReact.listensTo(react));
    return calcReact;
  }
}
