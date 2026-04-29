package necron.ui.animation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OutEase implements Ease {
  private final Ease ease;

  @Override
  public float ease(float progress) {
    return 1F - ease.ease(1F - progress);
  }
}
