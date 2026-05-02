package necron.ui.render;

import lombok.Value;
import lombok.With;
import lombok.val;
import necron.ui.Surface;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@With
@Value
public class BorderedRoundedRectRenderable implements Renderable {
  Renderable outer, inner;

  @Override
  public float getElevation() {
    return outer.getElevation();
  }

  @Override
  public void render(GuiGraphics gui) {
    outer.render(gui);
    inner.render(gui);
  }

  @Override
  public Renderable opacify(float factor) {
    return new BorderedRoundedRectRenderable(
      outer.opacify(factor),
      inner.opacify(factor)
    );
  }

  @Override
  public Renderable shadow(float elevationBelow) {
    return outer.shadow(elevationBelow);
  }

  @Override
  public Renderable translate(Vector2fc vec) {
    return new BorderedRoundedRectRenderable(
      outer.translate(vec),
      inner.translate(vec)
    );
  }

  @Override
  public Renderable scale(Vector2fc origin, float factor) {
    return new BorderedRoundedRectRenderable(
      outer.scale(origin, factor),
      inner.scale(origin, factor)
    );
  }

  public static BorderedRoundedRectRenderable createFor(RoundedRectRenderable outer, int borderColor) {
    val shrink = Surface.LINE_WIDTH.peek();
    return new BorderedRoundedRectRenderable(
      outer.withColor(borderColor),
      outer
        .withNw(new Vector2f(shrink).add(outer.getNw()))
        .withSe(new Vector2f(shrink).sub(outer.getSe()).negate())
        .withRadius(outer.getRadius() - shrink)
    );
  }
}
