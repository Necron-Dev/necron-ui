package necron.ui.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2fc;

public interface Renderable {
  float getElevation();

  void render(GuiGraphics gui, DeltaTracker delta);

  Renderable translate(Vector2fc vec);

  Renderable scale(Vector2fc origin, float factor);

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
