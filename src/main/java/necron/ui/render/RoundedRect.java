package necron.ui.render;

import lombok.Value;
import lombok.With;
import necron.ui.texture.Textures;
import necron.ui.util.ColorUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@With
@Value
public class RoundedRect implements Renderable {
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
    gui.pose().pushMatrix();
    gui.pose().translate(nw.x(), nw.y());
    gui.pose().scale(se.x() - nw.x(), se.y() - nw.y());
    gui.blit(
      RenderPipelines.GUI_TEXTURED,
      Textures.ROUNDED_RECT,
      0, 0, 0, 0, 1, 1, 1, 1
    );
    gui.pose().popMatrix();
  }

  @Override
  public Renderable translate(Vector2fc vec) {
    return withNw(new Vector2f(nw).add(vec)).withSe(new Vector2f(se).add(vec));
  }

  @Override
  public Renderable scale(Vector2fc origin, float factor) {
    return this
             .withRadius(radius * factor)
             .withNw(new Vector2f(nw).sub(origin).mul(factor).add(origin))
             .withSe(new Vector2f(se).sub(origin).mul(factor).add(origin));
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
