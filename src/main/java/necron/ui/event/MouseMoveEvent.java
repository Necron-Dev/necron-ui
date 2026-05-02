package necron.ui.event;

import lombok.Value;
import necron.ui.util.Maths;
import necron.ui.util.Transformable;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@Value
public class MouseMoveEvent implements Event, WithPosition, Transformable<MouseMoveEvent> {
  Vector2fc position;

  @Override
  public MouseMoveEvent translate(Vector2fc vec) {
    return new MouseMoveEvent(new Vector2f(position).add(vec));
  }

  @Override
  public MouseMoveEvent scale(Vector2fc origin, float factor) {
    return new MouseMoveEvent(Maths.scale(position, origin, factor));
  }
}
