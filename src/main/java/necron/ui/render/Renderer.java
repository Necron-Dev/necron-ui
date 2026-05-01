package necron.ui.render;

import lombok.val;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import static yqloss.E._any;

public class Renderer {
  public static void render9(
    GuiGraphics gui,
    Identifier identifier,
    float x,
    float y,
    float width,
    float height,
    float corner,
    int color
  ) {
    corner = Math.min(corner, width / 2F);
    corner = Math.min(corner, height / 2F);
    val x1 = x + corner;
    val x2 = x + width - corner;
    val mw = x2 - x1;
    val y1 = y + corner;
    val y2 = y + height - corner;
    val mh = y2 - y1;
    blit(
      gui, identifier,
      x, y, 0, 0,
      corner, corner, 0.25F, 0.25F,
      color
    );
    blit(
      gui, identifier,
      x2, y, 0.75F, 0,
      corner, corner, 0.25F, 0.25F,
      color
    );
    blit(
      gui, identifier,
      x, y2, 0, 0.75F,
      corner, corner, 0.25F, 0.25F,
      color
    );
    blit(
      gui, identifier,
      x2, y2, 0.75F, 0.75F,
      corner, corner, 0.25F, 0.25F,
      color
    );
    blit(
      gui, identifier,
      x1, y, 0.25F, 0,
      mw, corner, 0.5F, 0.25F,
      color
    );
    blit(
      gui, identifier,
      x1, y2, 0.25F, 0.75F,
      mw, corner, 0.5F, 0.25F,
      color
    );
    blit(
      gui, identifier,
      x, y1, 0, 0.25F,
      corner, mh, 0.25F, 0.5F,
      color
    );
    blit(
      gui, identifier,
      x2, y1, 0.75F, 0.25F,
      corner, mh, 0.25F, 0.5F,
      color
    );
    blit(
      gui, identifier,
      x1, y1, 0.25F, 0.25F,
      mw, mh, 0.5F, 0.5F,
      color
    );
  }

  public static void blit(
    GuiGraphics gui,
    Identifier identifier,
    float x,
    float y,
    float u,
    float v,
    float width,
    float height,
    float textureWidth,
    float textureHeight,
    int color
  ) {
    gui.pose().pushMatrix();
    gui.pose().translate(x, y);
    gui.pose().scale(width, height);
    var size = 1;
    while (
      size < 4096 && (
        _any
        || u != (int) u
        || v != (int) v
        || textureWidth != (int) textureWidth
        || textureHeight != (int) textureHeight
      )
    ) {
      size <<= 1;
      u *= 2;
      v *= 2;
      textureWidth *= 2;
      textureHeight *= 2;
    }
    gui.blit(
      RenderPipelines.GUI_TEXTURED, identifier,
      0, 0, u, v,
      1, 1, (int) textureWidth, (int) textureHeight,
      size, size,
      color
    );
    gui.pose().popMatrix();
  }
}
