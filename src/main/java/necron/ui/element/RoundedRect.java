package necron.ui.element;

import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.RenderEvent;
import necron.ui.event.UpdateEvent;
import necron.ui.layout.Box;
import necron.ui.layout.Pos;
import necron.ui.react.React;
import necron.ui.render.RoundedRectRenderable;
import org.joml.Vector2f;

import static necron.ui.layout.Box.size;
import static necron.ui.layout.Dim.px;
import static necron.ui.layout.Pos.anchor;

public class RoundedRect extends Node {
  private final React<Float> radius;
  private final React<Integer> color;

  public RoundedRect(
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
        renderEvent.getYieldRenderable().accept(new RoundedRectRenderable(
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

  private static class BackgroundKey {}

  public static void background(
    ChildrenConfiguration.ChildrenBuilderDsl dsl,
    React<Float> radius,
    React<Integer> color
  ) {
    dsl.add(
      BackgroundKey.class, (p, k) -> new RoundedRect(
        p, k,
        size(px(p.getWidth()), px(p.getHeight())),
        anchor(0, 0, 0, 0, 0, 0),
        p.up(0),
        radius,
        color
      )
    );
  }
}
