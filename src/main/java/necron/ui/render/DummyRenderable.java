package necron.ui.render;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2fc;

public class DummyRenderable implements Renderable {
  @Override
  public float getElevation() {
    return Float.POSITIVE_INFINITY;
  }

  @Override
  public void render(GuiGraphics gui) {
  }

  @Override
  public Renderable translate(Vector2fc vec) {
    return this;
  }

  @Override
  public Renderable scale(Vector2fc origin, float factor) {
    return this;
  }
}
