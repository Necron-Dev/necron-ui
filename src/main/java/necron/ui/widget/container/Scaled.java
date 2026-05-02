package necron.ui.widget.container;

import lombok.Builder;
import lombok.Getter;
import lombok.val;
import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.MetricsEvent;
import necron.ui.layout.Box;
import necron.ui.layout.Dim;
import necron.ui.layout.Pos;
import necron.ui.react.React;
import necron.ui.react.SubListReact;
import necron.ui.util.Transformable;
import necron.ui.widget.ChildrenConfiguration;
import necron.ui.widget.Container;
import necron.ui.widget.Element;
import necron.ui.widget.element.Node;
import org.joml.Vector2f;

import static necron.ui.layout.Box.size;
import static necron.ui.layout.Dim.*;
import static necron.ui.layout.Pos.auto;
import static necron.ui.react.React.useSubList;
import static necron.ui.util.fn.Fn2.fn;
import static yqloss.E.$;

public class Scaled extends Node implements Container {
  @Getter
  private final SubListReact<Element> children;

  private final React<Float> scale;

  @Getter
  private final React<Float> horizontalSpace, verticalSpace;

  @Builder(builderMethodName = "scaled")
  public Scaled(
    Container parent,
    Object key,
    Box.Size size,
    Pos positioning,
    React<Float> elevation,
    React<Float> scale,
    ChildrenConfiguration children
  ) {
    val list = this.children = useSubList();
    this.scale = scale;
    var width = size.getWidth();
    var height = size.getHeight();
    if (width instanceof Dim.Min) width = width.op((x, y) -> x * y, px(scale));
    if (height instanceof Dim.Min) height = height.op((x, y) -> x * y, px(scale));
    val horizontal = useSubList(list, x -> x.isWidthIndependent() ? x.getWidth() : fp(0));
    val vertical = useSubList(list, x -> x.isHeightIndependent() ? x.getHeight() : fp(0));
    super(
      parent,
      key,
      width.create(horizontal, $(parent.getHorizontalSpace()), false),
      height.create(vertical, $(parent.getVerticalSpace()), false),
      width.isIndependent(),
      height.isIndependent(),
      positioning,
      elevation
    );
    horizontalSpace = React.useCalc(
      fn((Float l, Float f) -> l * f),
      getWidth(), scale
    );
    verticalSpace = React.useCalc(
      fn((Float l, Float f) -> l * f),
      getHeight(), scale
    );
    list.setParent(ChildrenConfiguration.buildChildren(this, children), x -> x);
  }

  @Override
  public boolean dispatch(Context context, Event event, boolean handled) {
    switch (event) {
      case MetricsEvent _ -> {
        super.dispatch(context, event, handled);
        scale.get();
        Container.super.dispatch(context, event, handled);
        super.dispatch(context, event, handled);
        scale.get();
        return false;
      }

      case Transformable<?> transformable -> {
        super.dispatch(context, event, handled);
        val children = getChildren().peek();
        if (children.size() != 1) {
          throw new UnsupportedOperationException("Scaled must contain exactly one element");
        }
        val child = children.getFirst();
        val transformedEvent = transformable.scale(new Vector2f(), 1 / scale.peek());
        if (!(transformedEvent instanceof Event eventToDispatch)) return handled;
        handled |= child.dispatch(context, eventToDispatch, handled);
        return handled;
      }

      default -> {}
    }

    return super.dispatch(context, event, handled) | Container.super.dispatch(context, event, handled);
  }

  public static ScaledBuilder scaled(Container parent, Object key) {
    return new ScaledBuilder()
             .parent(parent)
             .key(key)
             .size(size(min(), min()))
             .positioning(auto())
             .elevation($($(parent.up(1)), fp(0)))
             .scale(fp(1))
             .children(_ -> _ -> {});
  }

  public static ScaledBuilder scaled(Container parent, Object key, ChildrenConfiguration children) {
    return scaled(parent, key).children(children);
  }
}
