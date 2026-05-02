package necron.ui.widget.container;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.val;
import necron.ui.NecronUi;
import necron.ui.Surface;
import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.MetricsEvent;
import necron.ui.event.PositionEvent;
import necron.ui.event.RenderEvent;
import necron.ui.layout.Axis;
import necron.ui.layout.Dim;
import necron.ui.layout.Padding;
import necron.ui.layout.Pos;
import necron.ui.react.BoxReact;
import necron.ui.react.React;
import necron.ui.react.SubListReact;
import necron.ui.react.SubReact;
import necron.ui.render.SolidRectRenderable;
import necron.ui.render.debug.DebugRectRenderable;
import necron.ui.style.Palette;
import necron.ui.util.Epsilon;
import necron.ui.widget.ChildrenConfiguration;
import necron.ui.widget.Container;
import necron.ui.widget.Element;
import necron.ui.widget.element.Node;
import org.joml.Vector2f;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static necron.ui.layout.Dim.*;
import static necron.ui.layout.Padding.padding;
import static necron.ui.layout.Pos.anchorCC;
import static necron.ui.layout.Pos.auto;
import static necron.ui.react.React.*;
import static necron.ui.util.fn.Fn2.fn;
import static necron.ui.util.fn.Fn3.fn;
import static necron.ui.util.fn.Fn4.fn;
import static yqloss.E.$;

public class Div extends Node implements Container {
  private final Axis axis;

  private final React<Float>
    paddingTop,
    paddingRight,
    paddingBottom,
    paddingLeft,
    widthWithPadding,
    heightWithPadding;

  @Getter
  private final SubListReact<Element> children;

  @Getter
  private final React<Float> alignment, horizontalSpace, verticalSpace;

  private final React<?> horizontalPositions, verticalPositions;

  private React<Float> createSpaceReact(
    React<Float> length,
    boolean major,
    Function<? super Element, ? extends Pos> elementPositioning,
    Function<? super Element, ? extends React<Float>> elementLength,
    Predicate<? super Element> elementIndependent
  ) {
    if (!major) return length;
    val childrenLength = useSubList(
      children,
      x -> elementPositioning.apply(x) instanceof Pos.Auto && elementIndependent.test(x)
           ? elementLength.apply(x)
           : fp(0)
    );
    return listDepend(
      useCalc(
        fn((Float lengthValue, List<React<Float>> listReact) -> {
          var space = lengthValue;
          for (val element : listReact) {
            space -= element.peek();
          }
          return space;
        }), length, childrenLength
      ), childrenLength
    );
  }

  private React<?> createPositionReact(
    boolean major,
    React<Float> innerSize,
    React<Float> padding,
    Function<? super Element, ? extends Pos> elementPositioning,
    Function<? super Element, ? extends React<Float>> getSize,
    Function<? super Element, ? extends SubReact<Float>> getSubReact
  ) {
    if (major) {
      @Value
      class ElementAndBox {
        Element element;
        BoxReact<Float> box;
      }
      val autoElements = useSubList(
        children,
        x -> {
          if (elementPositioning.apply(x) instanceof Pos.Auto) {
            val box = useBox(0F);
            getSubReact.apply(x).setParent(box, t -> t);
            return new ElementAndBox(x, box);
          }
          return null;
        }
      );
      return listDepend(
        useCalc(
          fn((List<ElementAndBox> items, Float paddingValue) -> {
            var position = paddingValue;
            for (val item : items) {
              if (item == null) continue;
              item.box.set(position);
              position += getSize.apply(item.element).peek();
            }
            return null;
          }),
          autoElements, padding
        ),
        useSubList(children, getSize)
      );
    } else {
      return useSubList(
        children,
        x -> {
          if (elementPositioning.apply(x) instanceof Pos.Auto) {
            val size = useCalc(
              fn((Float i, Float s, Float a, Float p) -> (i - s) * a + p),
              innerSize, getSize.apply(x), alignment, padding
            );
            getSubReact.apply(x).setParent(size, t -> t);
          }
          return null;
        }
      );
    }
  }

  @Builder(builderMethodName = "div")
  public Div(
    Container parent,
    Object key,
    Dim width,
    Dim height,
    Padding padding,
    Pos xPos,
    Pos yPos,
    React<Float> elevation,
    Axis axis,
    React<Float> alignment,
    ChildrenConfiguration children
  ) {
    if (width instanceof Dim.Min) {
      width = width
                .op(Float::sum, px(padding.getLeft()))
                .op(Float::sum, px(padding.getRight()));
    }
    if (height instanceof Dim.Min) {
      height = height
                 .op(Float::sum, px(padding.getTop()))
                 .op(Float::sum, px(padding.getBottom()));
    }
    this.axis = axis;
    paddingTop = padding.getTop();
    paddingRight = padding.getRight();
    paddingBottom = padding.getBottom();
    paddingLeft = padding.getLeft();
    this.alignment = alignment;
    val list = this.children = useSubList();
    val horizontal = useSubList(
      list,
      x -> x.getXPositioning() instanceof Pos.Auto && x.isWidthIndependent() ? x.getWidth() : fp(0)
    );
    val vertical = useSubList(
      list,
      x -> x.getYPositioning() instanceof Pos.Auto && x.isHeightIndependent() ? x.getHeight() : fp(0)
    );
    super(
      parent,
      key,
      width.create(horizontal, $(parent.getHorizontalSpace()), axis == Axis.X),
      height.create(vertical, $(parent.getVerticalSpace()), axis == Axis.Y),
      width.isIndependent(),
      height.isIndependent(),
      xPos,
      yPos,
      elevation
    );
    widthWithPadding = useCalc(
      fn((Float w, Float l, Float r) -> w - l - r),
      getWidth(), paddingLeft, paddingRight
    );
    heightWithPadding = useCalc(
      fn((Float h, Float t, Float b) -> h - t - b),
      getHeight(), paddingTop, paddingBottom
    );
    horizontalSpace = createSpaceReact(
      getInnerWidth(),
      axis == Axis.X,
      Element::getXPositioning,
      Element::getWidth,
      Element::isWidthIndependent
    );
    verticalSpace = createSpaceReact(
      getInnerHeight(),
      axis == Axis.Y,
      Element::getYPositioning,
      Element::getHeight,
      Element::isHeightIndependent
    );
    this.children.setParent(ChildrenConfiguration.buildChildren(this, children), x -> x);
    horizontalPositions = createPositionReact(
      axis == Axis.X,
      getInnerWidth(),
      paddingLeft,
      Element::getXPositioning,
      Element::getWidth,
      Element::getX
    );
    verticalPositions = createPositionReact(
      axis == Axis.Y,
      getInnerHeight(),
      paddingTop,
      Element::getYPositioning,
      Element::getHeight,
      Element::getY
    );
  }

  public Div(DivBuilder builder) {
    this(
      builder.parent,
      builder.key,
      builder.width,
      builder.height,
      builder.padding,
      builder.xPos,
      builder.yPos,
      builder.elevation,
      builder.axis,
      builder.alignment,
      builder.children
    );
  }

  public React<Float> getInnerWidth() {
    return widthWithPadding;
  }

  public React<Float> getInnerHeight() {
    return heightWithPadding;
  }

  @Override
  public boolean dispatch(Context context, Event event, boolean handled) {
    switch (event) {
      case MetricsEvent _ -> {
        updateMetrics(context, event, handled);
        Container.super.dispatch(context, event, handled);
        updateMetrics(context, event, handled);
        return false;
      }

      case PositionEvent _ -> {
        horizontalPositions.get();
        verticalPositions.get();
      }

      case RenderEvent renderEvent -> {
        if (NecronUi.isDebugMode()) {
          val innerWidth = getInnerWidth().peek();
          val innerHeight = getInnerHeight().peek();
          val x = paddingLeft.peek();
          val y = paddingTop.peek();
          renderEvent.getYieldRenderable().accept(new DebugRectRenderable(
            new Vector2f(x, y),
            new Vector2f(x + innerWidth, y + innerHeight),
            getInnerDebugRectColor()
          ));
        }
      }

      default -> {}
    }

    return super.dispatch(context, event, handled) | Container.super.dispatch(context, event, handled);
  }

  private void updateMetrics(Context context, Event event, boolean handled) {
    super.dispatch(context, event, handled);
    widthWithPadding.get();
    heightWithPadding.get();
    paddingTop.get();
    paddingRight.get();
    paddingBottom.get();
    paddingLeft.get();
    getHorizontalSpace().get();
    getVerticalSpace().get();
    getAlignment().get();
  }

  protected <T> T major(T x, T y) {
    return axis == Axis.X ? x : y;
  }

  protected <T> T cross(T x, T y) {
    return axis == Axis.X ? y : x;
  }

  public Node spacer(Object id, Dim length) {
    return Node.node(this, id)
             .width(axis == Axis.X ? length : px(0))
             .height(axis == Axis.Y ? length : px(0))
             .elevation(fp(Float.MAX_VALUE))
             .build();
  }

  public Node divider(Object id) {
    return new Node(
      Node.node(this, id)
        .width(axis == Axis.X ? px(Surface.LINE_WIDTH) : dependent(getWidth()))
        .height(axis == Axis.Y ? px(Surface.LINE_WIDTH) : dependent(getHeight()))
        .xPos(axis == Axis.X ? auto() : anchorCC())
        .yPos(axis == Axis.Y ? auto() : anchorCC())
        .elevation(up(Epsilon.FLOAT))
    ) {
      @Override
      public boolean dispatch(Context context, Event event, boolean handled) {
        if (event instanceof RenderEvent renderEvent) {
          renderEvent.getYieldRenderable().accept(new SolidRectRenderable(
            new Vector2f(),
            new Vector2f(getWidth().peek(), getHeight().peek()),
            Palette.GLOBAL.getDivider().peek(),
            getElevation().peek()
          ));
        }
        return super.dispatch(context, event, handled);
      }
    };
  }

  @Override
  protected int getDebugRectColor() {
    return 0xFF00FF00;
  }

  protected int getInnerDebugRectColor() {
    return 0xFF00FFFF;
  }

  public static DivBuilder div(Container parent, Object key) {
    return new DivBuilder()
             .parent(parent)
             .key(key)
             .width(min())
             .height(min())
             .padding(padding())
             .xPos(auto())
             .yPos(auto())
             .elevation($($(parent.up(1)), fp(0)))
             .axis(Axis.Y)
             .alignment(fp(0))
             .children(_ -> _ -> {});
  }

  public static DivBuilder div(Container parent, Object key, ChildrenConfiguration children) {
    return div(parent, key).children(children);
  }
}
