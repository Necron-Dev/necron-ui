package moe.nec.ui.layout;

import lombok.experimental.UtilityClass;
import moe.nec.ui.react.React;

@UtilityClass
public class Align {
  private final React<Float> START = React.of(0F);
  private final React<Float> CENTER = React.of(0.5F);
  private final React<Float> END = React.of(1F);

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
