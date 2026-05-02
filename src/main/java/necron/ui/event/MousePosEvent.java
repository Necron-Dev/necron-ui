package necron.ui.event;

import lombok.Value;
import necron.ui.util.Maths;
import necron.ui.util.Transformable;
import org.joml.Vector2f;
import org.joml.Vector2fc;

@Value
public class MousePosEvent implements Event, WithPosition, Transformable<MousePosEvent> {
  Vector2fc position;

  @Override
  public MousePosEvent translate(Vector2fc vec) {
    return new MousePosEvent(new Vector2f(position).add(vec));
  }

  @Override
  public MousePosEvent scale(Vector2fc origin, float factor) {
    return new MousePosEvent(Maths.scale(position, origin, factor));
  }
}
