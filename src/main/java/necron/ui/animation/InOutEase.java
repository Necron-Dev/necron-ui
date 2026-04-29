package necron.ui.animation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InOutEase implements Ease {
  private final Ease ease;

  @Override
  public float ease(float progress) {
    return progress < 0.5
           ? ease.ease(progress * 2F) / 2F
           : 1F - ease.ease(2F - progress * 2F) / 2F;
  }
}
