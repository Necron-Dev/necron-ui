package necron.ui.react;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

import static yqloss.E.$with;
import static yqloss.E._cast;

public class SubReact<T> extends CalcReact<T> {
  private static final Supplier<?> DUMMY_SUPPLIER = () -> null;

  private React<?> parent = null;
  private Supplier<? extends T> getter = _cast(DUMMY_SUPPLIER);

  protected SubReact(BiPredicate<? super T, ? super T> equals) {
    super(equals, null);
  }

  public <V> SubReact<T> setParent(
    React<? extends V> parent,
    Function<? super V, ? extends T> transformer
  ) {
    if (this.parent != null) cancelDependencies(this.parent);
    this.parent = parent;
    if (parent != null) dependsOn(parent);
    this.getter = $with(parent, p -> () -> transformer.apply(p.peek()));
    forceUpdate();
    return this;
  }

  @Override
  protected T calculate() {
    return getter.get();
  }
}
