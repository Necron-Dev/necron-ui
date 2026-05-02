package necron.ui.render;

import necron.ui.util.Transformable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public interface Renderable extends Transformable<Renderable> {
  float getElevation();

  void render(GuiGraphics gui, DeltaTracker delta);

  default Float getElevationScaleFactor() {
    return 0.005F * getElevation();
  }

  default Renderable opacify(float factor) {
    return this;
  }

  default Renderable shadow(float elevationBelow) {
    return null;
  }
}
