package necron.ui.element;

import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.RenderEvent;
import necron.ui.event.UpdateEvent;
import necron.ui.layout.Box;
import necron.ui.layout.Pos;
import necron.ui.react.React;
import necron.ui.render.RoundedRect;
import org.joml.Vector2f;

public class RoundedRectNode extends Node {
  private final React<Float> radius;
  private final React<Integer> color;

  public RoundedRectNode(
    Container parent,
    Object key,
    Box.Size size,
    Pos positioning,
    React<Float> elevation,
    React<Float> radius,
    React<Integer> color
  ) {
    super(parent, key, size, positioning, elevation);
    this.radius = radius;
    this.color = color;
  }

  @Override
  public boolean dispatch(Context context, Event event, boolean handled) {
    switch (event) {
      case UpdateEvent _ -> {
        radius.get();
        color.get();
      }

      case RenderEvent renderEvent -> {
        renderEvent.getYieldRenderable().accept(new RoundedRect(
          new Vector2f(0, 0),
          new Vector2f(getWidth().peek(), getHeight().peek()),
          radius.peek(),
          color.peek(),
          getElevation().peek()
        ));
      }

      default -> {}
    }

    return super.dispatch(context, event, handled);
  }
}
