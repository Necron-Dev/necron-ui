package necron.ui.render;

import lombok.Value;
import lombok.With;
import necron.ui.texture.Textures;
import necron.ui.util.ColorUtil;
import necron.ui.util.Maths;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@With
@Value
public class RoundedRectRenderable implements Renderable {
  Vector2fc nw, se;
  float radius;
  int color;
  float elevation;

  @Override
  public float getElevation() {
    return elevation;
  }

  @Override
  public void render(GuiGraphics gui, DeltaTracker delta) {
    Renderer.render9(
      gui, Textures.ROUNDED_RECT,
      nw.x(), nw.y(),
      se.x() - nw.x(), se.y() - nw.y(),
      radius,
      color
    );
  }

  @Override
  public Renderable translate(Vector2fc vec) {
    return withNw(new Vector2f(nw).add(vec)).withSe(new Vector2f(se).add(vec));
  }

  @Override
  public Renderable scale(Vector2fc origin, float factor) {
    return this
             .withRadius(radius * factor)
             .withNw(Maths.scale(nw, origin, factor))
             .withSe(Maths.scale(se, origin, factor));
  }

  @Override
  public Renderable opacify(float factor) {
    return withColor(ColorUtil.opacify(color, factor));
  }

  @Override
  public Renderable shadow(float elevationBelow) {
    return null;
  }
}
