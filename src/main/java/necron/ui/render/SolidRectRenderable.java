package necron.ui.render;

import lombok.Value;
import lombok.With;
import necron.ui.util.ColorUtil;
import necron.ui.util.Maths;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@With
@Value
public class SolidRectRenderable implements Renderable {
  Vector2fc nw, se;
  int color;
  float elevation;

  @Override
  public void render(GuiGraphics gui) {
    gui.pose().pushMatrix();
    gui.pose().translate(nw.x(), nw.y());
    gui.pose().scale(se.x() - nw.x(), se.y() - nw.y());
    gui.fill(0, 0, 1, 1, color);
    gui.pose().popMatrix();
  }

  @Override
  public Renderable translate(Vector2fc vec) {
    return withNw(new Vector2f(nw).add(vec)).withSe(new Vector2f(se).add(vec));
  }

  @Override
  public Renderable scale(Vector2fc origin, float factor) {
    return this
             .withNw(Maths.scale(nw, origin, factor))
             .withSe(Maths.scale(se, origin, factor));
  }

  @Override
  public Renderable opacify(float factor) {
    return withColor(ColorUtil.opacify(color, factor));
  }
}
