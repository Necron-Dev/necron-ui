package necron.ui.element;

import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.layout.Pos;
import necron.ui.react.React;
import necron.ui.react.SubReact;
import necron.ui.react.WithKey;
import necron.ui.util.SmartClosable;

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
}
