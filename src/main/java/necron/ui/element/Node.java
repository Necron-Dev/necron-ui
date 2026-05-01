package necron.ui.element;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import necron.ui.NecronUi;
import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.MetricsEvent;
import necron.ui.event.PositionEvent;
import necron.ui.event.RenderEvent;
import necron.ui.layout.Box;
import necron.ui.layout.Pos;
import necron.ui.react.React;
import necron.ui.react.SubReact;
import necron.ui.render.DebugRectRenderable;
import org.joml.Vector2f;

import static necron.ui.layout.Dim.fp;
import static necron.ui.react.React.*;
import static necron.ui.util.fn.Fn5.fn;
import static yqloss.E.$;

@Getter
@AllArgsConstructor
public class Node implements Element {
  private final Container parent;
  private final Object key;
  private final React<Float> width, height;
  private final boolean widthIndependent, heightIndependent;
  private final Pos positioning;
  private final React<Float> elevation;

  @Getter
  private final SubReact<Float>
    x = useSub(fp(0), x -> x),
    y = useSub(fp(0), x -> x);

  public Node(Container parent, Object key, Box.Size size, Pos positioning, React<Float> elevation) {
    this.parent = parent;
    this.key = key;
    val width = size.getWidth();
    val height = size.getHeight();
    this.width = width.create(useConstList(), $(parent.getHorizontalSpace()), false);
    widthIndependent = width.isIndependent();
    this.height = height.create(useConstList(), $(parent.getVerticalSpace()), false);
    heightIndependent = height.isIndependent();
    this.positioning = positioning;
    this.elevation = elevation;
    if (parent != null && positioning instanceof Pos.Anchor anchor) {
      x.setParent(
        useCalc(
          fn((Float p, Float s, Float r, Float a, Float o) -> r * p - a * s + o),
          parent.getWidth(), getWidth(), anchor.getRelativeX(), anchor.getAnchorX(), anchor.getOffsetX()
        ), x -> x
      );
      y.setParent(
        useCalc(
          fn((Float p, Float s, Float r, Float a, Float o) -> r * p - a * s + o),
          parent.getHeight(), getHeight(), anchor.getRelativeY(), anchor.getAnchorY(), anchor.getOffsetY()
        ), x -> x
      );
    }
  }

  @Override
  public boolean dispatch(Context context, Event event, boolean handled) {
    switch (event) {
      case MetricsEvent _ -> {
        width.get();
        height.get();
        elevation.get();
      }

      case PositionEvent _ -> {
        positioning.update();
        x.get();
        y.get();
      }

      case RenderEvent renderEvent -> {
        if (NecronUi.isDebugMode()) {
          renderEvent.getYieldRenderable().accept(new DebugRectRenderable(
            new Vector2f(),
            new Vector2f(getWidth().peek(), getHeight().peek()),
            getDebugRectColor()
          ));
        }
      }

      default -> {}
    }

    return false;
  }

  protected int getDebugRectColor() {
    return 0xFFFF0000;
  }
}
