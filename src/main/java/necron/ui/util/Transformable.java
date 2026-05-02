package necron.ui.util;

import org.joml.Vector2fc;

public interface Transformable<T> {
  T translate(Vector2fc vec);

  T scale(Vector2fc origin, float factor);
}
