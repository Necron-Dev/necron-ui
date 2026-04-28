package moe.nec.ui;

import lombok.experimental.UtilityClass;
import moe.nec.ui.react.BoxReact;

@UtilityClass
public class Timestamp {
  public final BoxReact<Long> NANO_TIME = new BoxReact<>(true, System.nanoTime());

  public void update() {
    NANO_TIME.set(System.nanoTime());
  }

  public long nano() {
    return NANO_TIME.get();
  }
}
