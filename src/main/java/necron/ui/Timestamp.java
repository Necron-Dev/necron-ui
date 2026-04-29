package necron.ui;

import necron.ui.react.BoxReact;

public class Timestamp {
  public static final BoxReact<Long> NANO_TIME = new BoxReact<>(true, System.nanoTime());

  public static void update() {
    NANO_TIME.set(System.nanoTime());
  }

  public static long nano() {
    return NANO_TIME.get();
  }
}
