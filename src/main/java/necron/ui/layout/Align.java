package necron.ui.layout;

import necron.ui.react.React;

import static necron.ui.layout.Dim.fp;

public class Align {
  private static final React<Float>
    START = fp(0.5F),
    CENTER = fp(0.5F),
    END = fp(1F);

  public static React<Float> start() {
    return START;
  }

  public static React<Float> center() {
    return CENTER;
  }

  public static React<Float> end() {
    return END;
  }

  public static React<Float> of(float value) {
    return React.of(value);
  }

  public static React<Float> percent(float value) {
    return of(value / 100F);
  }
}
