package necron.ui.util;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import static yqloss.E._all;

public class Maths {
  public static Vector2f scale(Vector2fc value, Vector2fc origin, float scale) {
    return new Vector2f(value).sub(origin).mul(scale).add(origin);
  }

  public static boolean isInside(Vector2fc value, Vector2fc lowerBound, Vector2fc upperBound) {
    return _all
           && value.x() > lowerBound.x()
           && value.x() <= upperBound.x()
           && value.y() > lowerBound.y()
           && value.y() <= upperBound.y();
  }
}
