package necron.ui.texture;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static yqloss.E._const;

public class RoundedRectGenerator {
  public static final byte[] IMAGE = _const(() -> pregenerate(1024));

  public interface PutPixel {
    void putPixel(int x, int y, int alpha);
  }

  public interface CornerFunc {
    boolean isInside(int x, int y, int size);
  }

  @SneakyThrows
  private static byte[] pregenerate(int size) {
    val image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    @Cleanup("dispose") val g = image.createGraphics();
    val unit = size >> 2;
    val power = 2;
    CornerFunc func = (x, y, s) -> {
      val xn = Math.pow((double) x / s, power);
      val yn = Math.pow((double) y / s, power);
      return xn + yn <= 1.0;
    };
    g.setComposite(AlphaComposite.Clear);
    g.fillRect(0, 0, size, size);
    g.setComposite(AlphaComposite.Src);
    g.setPaint(new Color(0xFFFFFFFF));
    g.fillRect(0, unit, size, size - unit - unit);
    g.fillRect(unit, 0, size - unit - unit, size);
    drawCorner(unit, func, (x, y, a) -> image.setRGB(unit - x - 1, unit - y - 1, (a + 1 << 24) - 1));
    drawCorner(unit, func, (x, y, a) -> image.setRGB(size - unit + x, size - unit + y, (a + 1 << 24) - 1));
    drawCorner(unit, func, (x, y, a) -> image.setRGB(unit - x - 1, size - unit + y, (a + 1 << 24) - 1));
    drawCorner(unit, func, (x, y, a) -> image.setRGB(size - unit + x, unit - y - 1, (a + 1 << 24) - 1));
    val stream = new ByteArrayOutputStream();
    ImageIO.write(image, "png", stream);
    return stream.toByteArray();
  }

  private static void drawCorner(int size, CornerFunc func, PutPixel putPixel) {
    for (var x = 0; x < size; x++) {
      for (var y = 0; y < size; y++) {
        if (func.isInside(x + 1, y + 1, size)) {
          putPixel.putPixel(x, y, 255);
          continue;
        }
        if (!func.isInside(x, y, size)) {
          putPixel.putPixel(x, y, 0);
          continue;
        }
        var counter = 0;
        var baseX = x << 5;
        var baseY = y << 5;
        var upscaled = size << 5;
        for (var i = 0; i < 32; i++) {
          for (var j = 0; j < 32; j++) {
            if (func.isInside(baseX + i, baseY + j, upscaled)) {
              counter++;
            }
          }
        }
        counter >>= 2;
        if (counter > 255) counter = 255;
        putPixel.putPixel(x, y, counter);
      }
    }
  }
}
