package necron.ui.animation;

import lombok.val;
import necron.ui.Timestamp;
import necron.ui.react.CalcReact;

import java.util.ArrayDeque;
import java.util.Deque;

public class Animation extends CalcReact<Float> {
  private record EaseAction(
    Ease ease,
    float magnitude,
    long start,
    long durationNs
  ) {}

  private float base;
  private final Deque<EaseAction> actions;

  public Animation(float base) {
    this.base = base;
    actions = new ArrayDeque<>();
    super(true);
    dependsOn(Timestamp.NANO_TIME);
  }

  public Animation next(Ease ease, float target, float startMs, float durationMs) {
    val durationNs = (long) (durationMs * 1_000_000L);
    if (durationNs > 0) {
      actions.add(new EaseAction(
        ease,
        target - base,
        Timestamp.nano() + (long) (startMs * 1_000_000L),
        durationNs
      ));
    }
    base = target;
    return this;
  }

  public Animation set(float target) {
    base = target;
    actions.clear();
    forceUpdate();
    return this;
  }

  public Animation complete() {
    return set(base);
  }

  public Animation interrupt() {
    return set(get());
  }

  @Override
  protected Float calculate() {
    val time = Timestamp.nano();
    actions.removeIf(action -> time - action.start > action.durationNs);
    var value = base;
    for (val action : actions) {
      val progress = (float) ((double) (time - action.start) / action.durationNs);
      value -= action.magnitude * (1F - action.ease.ease(progress));
    }
    return value;
  }
}
