package necron.ui.layout;

import necron.ui.element.Element;
import necron.ui.react.React;

import static necron.ui.layout.Dim.fp;
import static necron.ui.react.React.useCalc;
import static necron.ui.util.fn.Fn2.fn;

public class Elevation {
  public static React<Float> relative(Element parent, float value) {
    return React.useCalc(parent.getElevation(), p -> p + value);
  }

  public static React<Float> relative(Element parent, React<Float> value) {
    return useCalc(fn(Float::sum), parent.getElevation(), value);
  }

  public static React<Float> of(float value) {
    return fp(value);
  }
}
