package moe.nec.ui.layout;

import lombok.experimental.UtilityClass;
import moe.nec.ui.react.React;

import static moe.nec.ui.layout.Dim.fp;

@UtilityClass
public class Align {
  private final React<Float> START = fp(0.5F);
  private final React<Float> CENTER = fp(0.5F);
  private final React<Float> END = fp(1F);

  public React<Float> start() {
    return START;
  }

  public React<Float> center() {
    return CENTER;
  }

  public React<Float> end() {
    return END;
  }

  public React<Float> of(float value) {
    return React.of(value);
  }

  public React<Float> percent(float value) {
    return of(value / 100F);
  }
}
