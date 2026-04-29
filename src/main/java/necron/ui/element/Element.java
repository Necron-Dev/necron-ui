package necron.ui.element;

import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.layout.Elevation;
import necron.ui.react.React;
import necron.ui.util.SmartClosable;

import java.util.Queue;

public interface Element extends SmartClosable {
  Container getParent();

  React<Float> getWidth();

  boolean isWidthIndependent();

  React<Float> getHeight();

  boolean isHeightIndependent();

  React<Float> getElevation();

  default boolean dispatch(Context context, Event event, boolean handled) {
    return false;
  }

  @Override
  default void collectObjectsToClose(Queue<? super Object> queue) {
    queue.add(getWidth());
    queue.add(getHeight());
  }

  default React<Float> up(float elevation) {
    return Elevation.relative(this, elevation);
  }

  default React<Float> up(React<Float> elevation) {
    return Elevation.relative(this, elevation);
  }
}
