package moe.nec.ui.element;

import moe.nec.ui.context.Context;
import moe.nec.ui.event.Event;
import moe.nec.ui.layout.Elevation;
import moe.nec.ui.react.React;
import moe.nec.ui.util.SmartClosable;

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

  default React<Float> elevated(float elevation) {
    return Elevation.relative(this, elevation);
  }

  default React<Float> elevated(React<Float> elevation) {
    return Elevation.relative(this, elevation);
  }
}
