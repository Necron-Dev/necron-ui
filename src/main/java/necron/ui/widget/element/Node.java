package necron.ui.widget.element;

import lombok.Builder;
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
import necron.ui.render.debug.DebugCrossRenderable;
import necron.ui.render.debug.DebugRectRenderable;
import necron.ui.widget.Container;
import necron.ui.widget.Element;
import org.joml.Vector2f;

import static necron.ui.layout.Box.size;
import static necron.ui.layout.Dim.flex;
import static necron.ui.layout.Dim.fp;
import static necron.ui.layout.Pos.auto;
import static necron.ui.react.React.*;
import static necron.ui.util.fn.Fn5.fn;
import static yqloss.E.$;

@Getter
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

  @Builder(builderMethodName = "node")
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
    setupAnchorHook();
  }

  public Node(
    Container parent,
    Object key,
    React<Float> width,
    React<Float> height,
    boolean widthIndependent,
    boolean heightIndependent,
    Pos positioning,
    React<Float> elevation
  ) {
    this.parent = parent;
    this.key = key;
    this.width = width;
    this.height = height;
    this.widthIndependent = widthIndependent;
    this.heightIndependent = heightIndependent;
    this.positioning = positioning;
    this.elevation = elevation;
    setupAnchorHook();
  }

  private void setupAnchorHook() {
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
        getWidth().get();
        getHeight().get();
        getElevation().get();
      }

      case PositionEvent _ -> {
        getPositioning().update();
        getX().get();
        getY().get();
      }

      case RenderEvent renderEvent -> {
        if (NecronUi.isDebugMode()) {
          renderEvent.getYieldRenderable().accept(new DebugRectRenderable(
            new Vector2f(),
            new Vector2f(getWidth().peek(), getHeight().peek()),
            getDebugRectColor()
          ));

          if (positioning instanceof Pos.Anchor anchor) {
            renderEvent.getYieldRenderable().accept(new DebugCrossRenderable(
              new Vector2f(
                getWidth().peek() * anchor.getAnchorX().peek() - anchor.getOffsetX().peek(),
                getHeight().peek() * anchor.getAnchorY().peek() - anchor.getOffsetY().peek()
              ),
              getDebugAnchorCrossColor()
            ));
            renderEvent.getYieldRenderable().accept(new DebugCrossRenderable(
              new Vector2f(
                getWidth().peek() * anchor.getAnchorX().peek(),
                getHeight().peek() * anchor.getAnchorY().peek()
              ),
              getDebugOffsetCrossColor()
            ));
          }
        }
      }

      default -> {}
    }

    return false;
  }

  protected int getDebugRectColor() {
    return 0xFFFF0000;
  }

  protected int getDebugAnchorCrossColor() {
    return 0xFF00FF00;
  }

  protected int getDebugOffsetCrossColor() {
    return 0xFF0000FF;
  }

  public static NodeBuilder node(Container parent, Object key) {
    return new NodeBuilder()
             .parent(parent)
             .key(key)
             .size(size(flex(), flex()))
             .positioning(auto())
             .elevation($($(parent.up(1)), fp(0)));
  }
}
