package necron.ui.render;

import lombok.With;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@With
public record DebugRect(
  Vector2fc nw,
  Vector2fc se,
  float elevation,
  float opacity
) implements Renderable {
  @Override
  public Renderable opacify(float factor) {
    return withOpacity(opacity * factor);
  }

  @Override
  public Renderable scale(Vector2fc origin, float factor) {
    return this
             .withNw(new Vector2f(nw).sub(origin).mul(factor).add(origin))
             .withSe(new Vector2f(se).sub(origin).mul(factor).add(origin));
  }

  @Override
  public Renderable translate(Vector2fc vec) {
    return withNw(new Vector2f(nw).add(vec)).withSe(new Vector2f(se).add(vec));
  }

  @Override
  public float getElevation() {
    return elevation;
  }

  @Override
  public void render(GuiGraphics gui, DeltaTracker delta) {
    gui.renderOutline(
      (int) nw.x(),
      (int) nw.y(),
      (int) (se.x() - nw.x()),
      (int) (se.y() - nw.y()),
      -1
    );
  }
}
