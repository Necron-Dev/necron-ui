package necron.ui.render.debug;

import lombok.Value;
import lombok.With;
import lombok.val;
import necron.ui.Lazy;
import necron.ui.render.Renderable;
import necron.ui.util.Maths;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@With
@Value
public class DebugRectRenderable implements Renderable {
  Vector2fc nw, se;
  int color;

  @Override
  public Renderable scale(Vector2fc origin, float factor) {
    return this
             .withNw(Maths.scale(nw, origin, factor))
             .withSe(Maths.scale(se, origin, factor));
  }

  @Override
  public Renderable translate(Vector2fc vec) {
    return withNw(new Vector2f(nw).add(vec)).withSe(new Vector2f(se).add(vec));
  }

  @Override
  public float getElevation() {
    return Float.POSITIVE_INFINITY;
  }

  @Override
  public void render(GuiGraphics gui, DeltaTracker delta) {
    val guiScale = Lazy.MC.getWindow().getGuiScale();
    gui.pose().pushMatrix();
    gui.pose().scale(1F / guiScale);
    gui.renderOutline(
      (int) (guiScale * nw.x()),
      (int) (guiScale * nw.y()),
      (int) (guiScale * (se.x() - nw.x())),
      (int) (guiScale * (se.y() - nw.y())),
      color
    );
    gui.pose().popMatrix();
  }
}
