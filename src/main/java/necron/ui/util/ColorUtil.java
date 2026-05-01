package necron.ui.util;

import lombok.val;

public class ColorUtil {
  public static int opacify(int original, float factor) {
    val color = original & 0x00FFFFFF;
    val opacity = (original >> 24) & 0xFF;
    return color | ((int) (opacity * factor) << 24);
  }
}
