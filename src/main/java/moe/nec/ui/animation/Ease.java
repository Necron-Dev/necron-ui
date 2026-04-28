package moe.nec.ui.animation;

import net.minecraft.util.Mth;

public interface Ease {
  Ease LINEAR = new CachedEase(x -> x);
  Ease QUAD = new CachedEase(x -> x * x);
  Ease CUBIC = new CachedEase(x -> x * x * x);
  Ease QUART = new CachedEase(x -> x * x * x * x);
  Ease QUINT = new CachedEase(x -> x * x * x * x * x);
  Ease SINE = new CachedEase(x -> 1F - Mth.cos(x * Mth.HALF_PI));
  Ease EXPO = new CachedEase(x -> x == 0F ? 0F : (float) Math.pow(2D, 10D * x - 10D));
  Ease CIRC = new CachedEase(x -> 1F - Mth.sqrt(1F - x * x));
  Ease BACK = new CachedEase(x -> (2.70158F * x - 1.70158F) * x * x);
  Ease ELASTIC = new CachedEase(
    x -> x == 0F ? 0F :
         x == 1F ? 1F :
         (float) (-Math.pow(2D, 10D * x - 10D) * Math.sin((10D * x - 10.75) * Math.PI * 2D / 3D))
  );

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
}
