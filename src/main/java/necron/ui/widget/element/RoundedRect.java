package necron.ui.widget.element;

import lombok.Builder;
import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.RenderEvent;
import necron.ui.event.UpdateEvent;
import necron.ui.layout.Dim;
import necron.ui.layout.Pos;
import necron.ui.react.React;
import necron.ui.render.BorderedRoundedRectRenderable;
import necron.ui.render.RoundedRectRenderable;
import necron.ui.widget.ChildrenConfiguration;
import necron.ui.widget.Container;
import org.joml.Vector2f;

import static necron.ui.layout.Dim.*;
import static necron.ui.layout.Pos.anchorLL;
import static necron.ui.layout.Pos.auto;
import static necron.ui.react.React.useConst;
import static yqloss.E.$;

public class RoundedRect extends Node {
  private final React<Float> radius;
  private final React<Integer> innerColor;
  private final React<Integer> outerColor;

  @Builder(builderMethodName = "roundedRect")
  public RoundedRect(
    Container parent,
    Object key,
    Dim width,
    Dim height,
    Pos xPos,
    Pos yPos,
    React<Float> elevation,
    React<Float> radius,
    React<Integer> innerColor,
    React<Integer> outerColor
  ) {
    super(parent, key, width, height, xPos, yPos, elevation);
    this.radius = radius;
    this.innerColor = innerColor;
    this.outerColor = outerColor;
  }

  public RoundedRect(RoundedRectBuilder builder) {
    this(
      builder.parent,
      builder.key,
      builder.width,
      builder.height,
      builder.xPos,
      builder.yPos,
      builder.elevation,
      builder.radius,
      builder.innerColor,
      builder.outerColor
    );
  }

  @Override
  public boolean dispatch(Context context, Event event, boolean handled) {
    switch (event) {
      case UpdateEvent _ -> {
        radius.get();
        innerColor.get();
        outerColor.get();
      }

      case RenderEvent renderEvent -> {
        renderEvent.getYieldRenderable().accept(
          BorderedRoundedRectRenderable.createFor(
            new RoundedRectRenderable(
              new Vector2f(0, 0),
              new Vector2f(getWidth().peek(), getHeight().peek()),
              radius.peek(),
              innerColor.peek(),
              getElevation().peek()
            ),
            outerColor.peek()
          )
        );
      }

      default -> {}
    }

    return super.dispatch(context, event, handled);
  }

  private static class BackgroundKey {}

  public static void background(
    ChildrenConfiguration.ChildrenBuilderDsl dsl,
    React<Float> radius,
    React<Integer> innerColor,
    React<Integer> outerColor
  ) {
    dsl.add(
      BackgroundKey.class, (p, k) -> new RoundedRect(
        p, k,
        px(p.getWidth()), px(p.getHeight()),
        anchorLL(), anchorLL(),
        p.up(0),
        radius,
        innerColor,
        outerColor
      )
    );
  }

  public static RoundedRectBuilder roundedRect(Container parent, Object key) {
    return new RoundedRectBuilder()
             .parent(parent)
             .key(key)
             .width(flex())
             .height(flex())
             .xPos(auto())
             .yPos(auto())
             .elevation($($(parent.up(1)), fp(0)))
             .radius(fp(0))
             .innerColor(useConst(0))
             .outerColor(useConst(-1));
  }
}
