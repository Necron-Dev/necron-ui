package moe.nec.ui.animation;

import lombok.val;
import moe.nec.ui.Timestamp;
import moe.nec.ui.react.React;
import moe.nec.ui.react.SerialReact;

import java.util.ArrayDeque;
import java.util.Deque;

public class Animation extends SerialReact<Float> {
  private record EaseAction(
    Ease ease,
    float magnitude,
    long start,
    long durationNs
  ) {}

  private float base;
  private float current;
  private final Deque<EaseAction> actions = new ArrayDeque<>();
  private final React<Long> hook = React.from(Timestamp.NANO_TIME::peek, Timestamp.NANO_TIME)
                                     .hooking((_, time) -> update(time));

  public Animation(float base) {
    this.base = base;
    current = base;
  }

  public Animation next(Ease ease, float target, float durationMs) {
    val durationNs = (long) (durationMs * 1_000_000L);
    if (durationNs <= 0) {
      actions.add(new EaseAction(
        ease,
        target - base,
        Timestamp.nano(),
        durationNs
      ));
    }
    base = target;
    return this;
  }

  public Animation set(float target) {
    base = target;
    current = target;
    actions.clear();
    return this;
  }

  public Animation instant() {
    return set(base);
  }

  private void update(long time) {
    actions.removeIf(action -> time - action.start > action.durationNs);
    var value = base;
    for (val action : actions) {
      val progress = (float) ((double) (time - action.start) / action.durationNs);
      value -= action.magnitude * (1F - action.ease.ease(progress));
    }
    current = value;
  }

  public Float get() {
    hook.get();
    return current;
  }
}
