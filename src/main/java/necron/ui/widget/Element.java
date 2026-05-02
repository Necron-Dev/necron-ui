package necron.ui.widget;

import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.WithPosition;
import necron.ui.layout.Pos;
import necron.ui.react.React;
import necron.ui.react.SubReact;
import necron.ui.react.WithKey;
import necron.ui.util.Maths;
import necron.ui.util.SmartClosable;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.Queue;

import static necron.ui.react.React.useCalc;
import static necron.ui.util.fn.Fn2.fn;

public interface Element extends SmartClosable, WithKey {
  Container getParent();

  React<Float> getWidth();

  boolean isWidthIndependent();

  React<Float> getHeight();

  boolean isHeightIndependent();

  React<Float> getElevation();

  Pos getPositioning();

  SubReact<Float> getX();

  SubReact<Float> getY();

  default boolean dispatch(Context context, Event event, boolean handled) {
    return false;
  }

  @Override
  default void collectObjectsToClose(Queue<? super Object> queue) {
    queue.add(getWidth());
    queue.add(getHeight());
  }

  default React<Float> up(float elevation) {
    return React.useCalc(getElevation(), p -> p + elevation);
  }

  default React<Float> up(React<Float> elevation) {
    return useCalc(fn(Float::sum), getElevation(), elevation);
  }

  default boolean isInside(Vector2fc pos) {
    return Maths.isInside(pos, new Vector2f(), new Vector2f(getWidth().peek(), getHeight().peek()));
  }

  default boolean isInside(WithPosition object) {
    return isInside(object.getPosition());
  }
}
