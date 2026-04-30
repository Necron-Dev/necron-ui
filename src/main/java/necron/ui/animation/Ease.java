package necron.ui.animation;

import lombok.val;
import net.minecraft.util.Mth;

public interface Ease {
  Ease
    LINEAR = new CachedEase(x -> x),
    QUAD = new CachedEase(x -> x * x),
    CUBIC = new CachedEase(x -> x * x * x),
    QUART = new CachedEase(x -> x * x * x * x),
    QUINT = new CachedEase(x -> x * x * x * x * x),
    SINE = new CachedEase(x -> 1F - (float) Math.cos(x * Math.PI / 2D)),
    EXPO = new CachedEase(spread(x -> (float) Math.pow(2D, 10D * x - 10D))),
    CIRC = new CachedEase(x -> 1F - Mth.sqrt(1F - x * x)),
    BACK = new CachedEase(x -> (2.70158F * x - 1.70158F) * x * x),
    ELASTIC = new CachedEase(spread(
      x -> (float) (-Math.pow(2D, 10D * x - 10D) * Math.sin((10D * x - 10.75) * Math.PI * 2D / 3D))
    ));

  float ease(float progress);

  default Ease in() {
    return this;
  }

  default Ease out() {
    return new OutEase(this);
  }

  default Ease inOut() {
    return new InOutEase(this);
  }

  static Ease spread(Ease ease) {
    val at0 = ease.ease(0F);
    val diff = ease.ease(1F) - at0;
    return x -> (ease.ease(x) - at0) / diff;
  }
}
