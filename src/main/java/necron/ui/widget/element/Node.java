package necron.ui.widget.element;

import lombok.Builder;
import lombok.Getter;
import necron.ui.NecronUi;
import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.MetricsEvent;
import necron.ui.event.PositionEvent;
import necron.ui.event.RenderEvent;
import necron.ui.layout.Dim;
import necron.ui.layout.Pos;
import necron.ui.react.React;
import necron.ui.react.SubReact;
import necron.ui.render.debug.DebugCrossRenderable;
import necron.ui.render.debug.DebugRectRenderable;
import necron.ui.widget.Container;
import necron.ui.widget.Element;
import org.joml.Vector2f;

import static necron.ui.layout.Dim.flex;
import static necron.ui.layout.Dim.fp;
import static necron.ui.layout.Pos.auto;
import static necron.ui.react.React.*;
import static necron.ui.util.fn.Fn5.fn;
import static yqloss.E.$;
import static yqloss.E._all;

@Getter
public class Node implements Element {
  private final Container parent;
  private final Object key;
  private final React<Float> width, height;
  private final boolean widthIndependent, heightIndependent;
  private final Pos xPositioning, yPositioning;
  private final React<Float> elevation;

  @Getter
  private final SubReact<Float>
    x = useSub(fp(0), x -> x),
    y = useSub(fp(0), x -> x);

  @Builder(builderMethodName = "node")
  public Node(
    Container parent,
    Object key,
    Dim width,
    Dim height,
    Pos xPos,
    Pos yPos,
    React<Float> elevation
  ) {
    this.parent = parent;
    this.key = key;
    this.width = width.create(useConstList(), $(parent.getHorizontalSpace()), false);
    widthIndependent = width.isIndependent();
    this.height = height.create(useConstList(), $(parent.getVerticalSpace()), false);
    heightIndependent = height.isIndependent();
    this.xPositioning = xPos;
    this.yPositioning = yPos;
    this.elevation = elevation;
    setupAnchorHook();
  }

  public Node(NodeBuilder builder) {
    this(
      builder.parent,
      builder.key,
      builder.width,
      builder.height,
      builder.xPos,
      builder.yPos,
      builder.elevation
    );
  }

  public Node(
    Container parent,
    Object key,
    React<Float> width,
    React<Float> height,
    boolean widthIndependent,
    boolean heightIndependent,
    Pos xPositioning,
    Pos yPositioning,
    React<Float> elevation
  ) {
    this.parent = parent;
    this.key = key;
    this.width = width;
    this.height = height;
    this.widthIndependent = widthIndependent;
    this.heightIndependent = heightIndependent;
    this.xPositioning = xPositioning;
    this.yPositioning = yPositioning;
    this.elevation = elevation;
    setupAnchorHook();
  }

  private void setupAnchorHook() {
    if (parent == null) return;
    if (xPositioning instanceof Pos.Anchor anchor) {
      x.setParent(
        useCalc(
          fn((Float p, Float s, Float r, Float a, Float o) -> r * p - a * s + o),
          parent.getWidth(), getWidth(), anchor.getRelative(), anchor.getAnchor(), anchor.getOffset()
        ), x -> x
      );
    }
    if (yPositioning instanceof Pos.Anchor anchor) {
      y.setParent(
        useCalc(
          fn((Float p, Float s, Float r, Float a, Float o) -> r * p - a * s + o),
          parent.getHeight(), getHeight(), anchor.getRelative(), anchor.getAnchor(), anchor.getOffset()
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
        getXPositioning().update();
        getYPositioning().update();
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

          if (
            _all
            && xPositioning instanceof Pos.Anchor anchorX
            && yPositioning instanceof Pos.Anchor anchorY
          ) {
            renderEvent.getYieldRenderable().accept(new DebugCrossRenderable(
              new Vector2f(
                getWidth().peek() * anchorX.getAnchor().peek() - anchorX.getOffset().peek(),
                getHeight().peek() * anchorY.getAnchor().peek() - anchorY.getOffset().peek()
              ),
              getDebugAnchorCrossColor()
            ));
            renderEvent.getYieldRenderable().accept(new DebugCrossRenderable(
              new Vector2f(
                getWidth().peek() * anchorX.getAnchor().peek(),
                getHeight().peek() * anchorY.getAnchor().peek()
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
             .width(flex())
             .height(flex())
             .xPos(auto())
             .yPos(auto())
             .elevation($($(parent.up(1)), fp(0)));
  }
}
