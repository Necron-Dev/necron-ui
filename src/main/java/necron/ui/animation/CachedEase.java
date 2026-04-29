package necron.ui.animation;

public class CachedEase implements Ease {
  private final Ease ease;
  private final Ease in;
  private final Ease out;
  private final Ease inOut;

  public CachedEase(Ease ease) {
    this.ease = ease;
    in = ease.in();
    out = ease.out();
    inOut = ease.inOut();
  }

  @Override
  public float ease(float progress) {
    return ease.ease(progress);
  }

  @Override
  public Ease in() {
    return in;
  }

  @Override
  public Ease out() {
    return out;
  }

  @Override
  public Ease inOut() {
    return inOut;
  }
}
