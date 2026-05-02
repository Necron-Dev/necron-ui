package necron.ui.render;

import lombok.Value;
import lombok.With;
import necron.ui.util.ColorUtil;
import necron.ui.util.Maths;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@With
@Value
public class TextLineRenderable implements Renderable {
  Vector2fc pos;
  FormattedCharSequence text;
  Font font;
  float height;
  int color;
  float elevation;

  @Override
  public void render(GuiGraphics gui) {
    gui.pose().pushMatrix();
    gui.pose().translate(pos.x(), pos.y());
    gui.pose().scale(height / 8F);
    gui.drawString(font, text, 0, 0, color, false);
    gui.pose().popMatrix();
  }

  @Override
  public Renderable translate(Vector2fc vec) {
    return withPos(new Vector2f(pos).add(vec));
  }

  @Override
  public Renderable scale(Vector2fc origin, float factor) {
    return this
             .withPos(Maths.scale(pos, origin, factor))
             .withHeight(height * factor);
  }

  @Override
  public Renderable opacify(float factor) {
    return withColor(ColorUtil.opacify(color, factor));
  }
}
