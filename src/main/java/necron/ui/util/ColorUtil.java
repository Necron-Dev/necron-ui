package necron.ui.util;

import lombok.val;

public class ColorUtil {
  public static int opacify(int original, float factor) {
    val color = original & 0x00FFFFFF;
    val opacity = (original >> 24) & 0xFF;
    return color | ((int) (opacity * factor) << 24);
  }

  public static int compose(int a, int r, int g, int b) {
    return (a << 24) | (r << 16) | (g << 8) | b;
  }

  public static int compose(float a, float r, float g, float b) {
    return compose(
      (int) (a * 255F),
      (int) (r * 255F),
      (int) (g * 255F),
      (int) (b * 255F)
    );
  }

  public static int ai(int color) {
    return (color >> 24) & 0xFF;
  }

  public static int ri(int color) {
    return (color >> 16) & 0xFF;
  }

  public static int gi(int color) {
    return (color >> 8) & 0xFF;
  }

  public static int bi(int color) {
    return color & 0xFF;
  }

  public static float af(int color) {
    return ai(color) / 255F;
  }

  public static float rf(int color) {
    return ri(color) / 255F;
  }

  public static float gf(int color) {
    return gi(color) / 255F;
  }

  public static float bf(int color) {
    return bi(color) / 255F;
  }
}
