package necron.ui;

import lombok.val;
import necron.ui.react.BoxReact;
import necron.ui.react.CalcReact;
import necron.ui.react.React;

import java.util.Objects;

import static necron.ui.react.React.useCalc;

public class Timestamp {
  public static final BoxReact<Long> NANO_TIME = new BoxReact<>(Objects::equals, System.nanoTime());

  public static void update() {
    NANO_TIME.set(System.nanoTime());
  }

  public static long nano() {
    return NANO_TIME.peek();
  }

  public static CalcReact<Long> timer(React<Float> intervalMs) {
    val intervalNs = useCalc(intervalMs, x -> (long) ((double) x * 1_000_000));
    return new CalcReact<Long>(Objects::equals) {
      private long time = nano();
      private long accumulator = 0;
      private long serial = 0;

      @Override
      public void update() {
        val now = nano();
        if (now - time < 0) {
          accumulator = 0;
        } else {
          accumulator += now - time;
        }
        time = now;
        super.update();
      }

      @Override
      protected Long calculate() {
        val interval = intervalNs.peek();
        if (accumulator >= interval) {
          accumulator -= interval;
          return ++serial;
        }
        return serial;
      }
    }.dependsOn(intervalNs, Timestamp.NANO_TIME);
  }
}
