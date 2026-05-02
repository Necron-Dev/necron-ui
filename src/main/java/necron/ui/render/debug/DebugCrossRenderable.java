package necron.ui.render.debug;

import lombok.Value;
import lombok.With;
import lombok.val;
import necron.ui.Lazy;
import necron.ui.render.Renderable;
import necron.ui.util.Maths;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@With
@Value
public class DebugCrossRenderable implements Renderable {
  Vector2fc pos;
  int color;

  @Override
  public Renderable scale(Vector2fc origin, float factor) {
    return this.withPos(Maths.scale(pos, origin, factor));
  }

  @Override
  public Renderable translate(Vector2fc vec) {
    return this.withPos(new Vector2f(pos).add(vec));
  }

  @Override
  public float getElevation() {
    return Float.POSITIVE_INFINITY;
  }

  @Override
  public void render(GuiGraphics gui) {
    val guiScale = Lazy.MC.getWindow().getGuiScale();
    gui.pose().pushMatrix();
    gui.pose().scale(1F / guiScale);
    val length = 10;
    gui.hLine(
      (int) (guiScale * (pos.x() - length)),
      (int) (guiScale * (pos.x() + length)),
      (int) (guiScale * pos.y()),
      color
    );
    gui.vLine(
      (int) (guiScale * pos.x()),
      (int) (guiScale * (pos.y() - length)),
      (int) (guiScale * (pos.y() + length)),
      color
    );
    gui.pose().popMatrix();
  }
}
