package necron.ui.react;

import lombok.val;
import necron.ui.util.MappedList;
import necron.ui.util.fn.Fn;

import java.util.ArrayList;
import java.util.Arrays;
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

  static UpdaterReact useUpdater() {
    return new UpdaterReact();
  }

  static <T> ConstReact<T> useConst(T value) {
    return new ConstReact<>(value);
  }

  static <T> BoxReact<T> useBox(BiPredicate<? super T, ? super T> equals, T value) {
    return new BoxReact<>(equals, value);
  }

  static <T> BoxReact<T> useBox(T value) {
    return useBox(Objects::equals, value);
  }

  static <T> CalcReact<T> useCalc(
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

  static <A, T> CalcReact<T> useCalc(
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

  static <T> CalcReact<T> useCalc(
    BiPredicate<? super T, ? super T> equals,
    Fn<? extends T> value,
    React<?>... dependencies
  ) {
    return useCalc(
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

  static <A, T> CalcReact<T> useCalc(
    React<? extends A> dependency,
    Function<? super A, ? extends T> value
  ) {
    return useCalc(Objects::equals, dependency, value);
  }

  static <T> CalcReact<T> useCalc(
    Supplier<? extends T> value,
    React<?>... dependencies
  ) {
    return useCalc(Objects::equals, value, dependencies);
  }

  static <T> CalcReact<T> useCalc(
    Fn<? extends T> value,
    React<?>... dependencies
  ) {
    return useCalc(Objects::equals, value, dependencies);
  }

  static <T> CalcReact<T> useListen(
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

  static <A, T> CalcReact<T> useListen(
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

  static <T> CalcReact<T> useListen(
    BiPredicate<? super T, ? super T> equals,
    Fn<? extends T> value,
    React<?>... dependencies
  ) {
    return useListen(
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

  static <T> CalcReact<T> useListen(
    Supplier<? extends T> value,
    React<?>... dependencies
  ) {
    return useListen(Objects::equals, value, dependencies);
  }

  static <A, T> CalcReact<T> useListen(
    React<? extends A> dependency,
    Function<? super A, ? extends T> value
  ) {
    return useListen(Objects::equals, dependency, value);
  }

  static <T> CalcReact<T> useListen(
    Fn<? extends T> value,
    React<?>... dependencies
  ) {
    return useListen(Objects::equals, value, dependencies);
  }

  static <T> SubReact<T> useSub(BiPredicate<? super T, ? super T> equals) {
    return new SubReact<>(equals);
  }

  static <T> SubReact<T> useSub() {
    return useSub(Objects::equals);
  }

  static <T, V> SubReact<T> useSub(
    BiPredicate<? super T, ? super T> equals,
    React<? extends V> parent,
    Function<? super V, ? extends T> transformer
  ) {
    return React.<T>useSub(equals).setParent(parent, transformer);
  }

  static <T, V> SubReact<T> useSub(
    React<? extends V> parent,
    Function<? super V, ? extends T> transformer
  ) {
    return React.<T>useSub().setParent(parent, transformer);
  }

  static <T> ConstListReact<T> useConstList(List<? extends T> list) {
    return new ConstListReact<>(list);
  }

  @SafeVarargs
  static <T> ConstListReact<T> useConstList(T... list) {
    return new ConstListReact<>(Arrays.asList(list));
  }

  static <T> SubListReact<T> useSubList() {
    return new SubListReact<>();
  }

  static <V, T> SubListReact<T> useSubList(
    ListReact<? extends V> parent,
    Function<? super V, ? extends T> transformer
  ) {
    return React.<T>useSubList().setParent(parent, transformer);
  }

  static <V, T> ListReact<T> useMapList(
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

  static <T extends WithKey> CalcListReact<T> useCalcList(
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
