package moe.nec.ui.layout;

import lombok.experimental.UtilityClass;
import moe.nec.ui.element.Element;
import moe.nec.ui.react.React;

import static moe.nec.ui.layout.Dim.fp;
import static moe.nec.ui.react.React.react;
import static moe.nec.ui.util.fn.Fn1.fn;
import static moe.nec.ui.util.fn.Fn2.fn;

@UtilityClass
public class Elevation {
  public React<Float> relative(Element parent, float value) {
    return react(fn((Float p) -> p + value), parent.getElevation());
  }

  public React<Float> relative(Element parent, React<Float> value) {
    return react(fn(Float::sum), parent.getElevation(), value);
  }

  public React<Float> of(float value) {
    return fp(value);
  }
}
