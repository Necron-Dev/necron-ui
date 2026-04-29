package necron.ui.react;

import java.util.function.Supplier;

public class FnReact<T> extends CalcReact<T> {
  private final Supplier<? extends T> calculate;

  public FnReact(boolean checkForEquals, Supplier<? extends T> calculate) {
    this.calculate = calculate;
    super(checkForEquals);
  }

  @Override
  protected T calculate() {
    return calculate.get();
  }
}
