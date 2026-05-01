package necron.ui.element;

import lombok.Getter;
import lombok.val;
import necron.ui.NecronUi;
import necron.ui.context.Context;
import necron.ui.event.*;
import necron.ui.layout.Axis;
import necron.ui.layout.Box;
import necron.ui.layout.Dim;
import necron.ui.react.React;
import necron.ui.react.SubListReact;
import necron.ui.render.DebugRect;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static necron.ui.layout.Box.size;
import static necron.ui.layout.Dim.fp;
import static necron.ui.layout.Dim.px;
import static necron.ui.react.React.*;
import static necron.ui.util.fn.Fn2.fn;
import static necron.ui.util.fn.Fn3.fn;
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

  private final WeakHashMap<Object, Vector2fc> cachedPositions = new WeakHashMap<>();

  private React<Float> createSpaceReact(
    React<Float> length,
    boolean major,
    Function<? super Element, ? extends React<Float>> elementLength,
    Predicate<? super Element> elementIndependent
  ) {
    if (!major) return length;
    val childrenLength = useSubList(
      children,
      x -> elementIndependent.test(x)
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

  public Div(
    Container parent,
    Object key,
    Box.SizePadding sizePadding,
    React<Float> elevation,
    Axis axis,
    React<Float> alignment,
    ChildrenConfiguration children
  ) {
    if (sizePadding.getWidth() instanceof Dim.Min) sizePadding = sizePadding.padWidth();
    if (sizePadding.getHeight() instanceof Dim.Min) sizePadding = sizePadding.padHeight();
    this.axis = axis;
    paddingTop = sizePadding.getPaddingTop();
    paddingRight = sizePadding.getPaddingRight();
    paddingBottom = sizePadding.getPaddingBottom();
    paddingLeft = sizePadding.getPaddingLeft();
    this.alignment = alignment;
    val width = sizePadding.getWidth();
    val height = sizePadding.getHeight();
    val list = this.children = useSubList();
    val horizontal = useSubList(list, x -> x.isWidthIndependent() ? x.getWidth() : fp(0));
    val vertical = useSubList(list, x -> x.isHeightIndependent() ? x.getHeight() : fp(0));
    super(
      parent,
      key,
      width.create(horizontal, $(parent.getHorizontalSpace()), axis == Axis.X),
      height.create(vertical, $(parent.getVerticalSpace()), axis == Axis.Y),
      elevation,
      width.isIndependent(),
      height.isIndependent()
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
      Element::getWidth,
      Element::isWidthIndependent
    );
    verticalSpace = createSpaceReact(
      getInnerHeight(),
      axis == Axis.Y,
      Element::getHeight,
      Element::isHeightIndependent
    );
    this.children.setParent(ChildrenConfiguration.buildChildren(this, children), x -> x);
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
      case ContentEvent _ -> {
        children.get();
      }

      case MetricsEvent _ -> {
        updateMetrics(context, event, handled);
        Container.super.dispatch(context, event, handled);
        updateMetrics(context, event, handled);
        return false;
      }

      case PositionEvent _ -> {
        cachedPositions.clear();
        val innerWidth = getInnerWidth().peek();
        val innerHeight = getInnerHeight().peek();
        val x = paddingLeft.peek();
        val y = paddingTop.peek();
        val crossSpace = cross(innerWidth, innerHeight);
        var accumulated = 0F;
        for (val child : getChildren().peek()) {
          val majorSize = major(child.getWidth(), child.getHeight()).peek();
          val crossSize = cross(child.getWidth(), child.getHeight()).peek();
          val major = accumulated;
          val cross = (crossSpace - crossSize) * alignment.peek();
          val childX = x + major(major, cross);
          val childY = y + cross(major, cross);
          cachedPositions.put(child.getKey(), new Vector2f(childX, childY));
          accumulated += majorSize;
        }
      }

      case RenderEvent renderEvent -> {
        if (NecronUi.isDebugMode()) {
          val innerWidth = getInnerWidth().peek();
          val innerHeight = getInnerHeight().peek();
          val x = paddingLeft.peek();
          val y = paddingTop.peek();
          renderEvent.getYieldRenderable().accept(new DebugRect(
            new Vector2f(x, y),
            new Vector2f(x + innerWidth, y + innerHeight),
            getElevation().peek(),
            1F
          ));
        }
        for (val child : getChildren().peek()) {
          val pos = cachedPositions.get(child.getKey());
          child.dispatch(
            context,
            new RenderEvent(
              renderable -> renderEvent.getYieldRenderable().accept(
                renderable.translate(pos)
              )
            ),
            false
          );
        }
        return false;
      }

      default -> {}
    }

    return Container.super.dispatch(context, event, handled);
  }

  private void updateMetrics(Context context, Event event, boolean handled) {
    super.dispatch(context, event, handled);
    widthWithPadding.get();
    heightWithPadding.get();
    paddingTop.get();
    paddingRight.get();
    paddingBottom.get();
    paddingLeft.get();
    horizontalSpace.get();
    verticalSpace.get();
    alignment.get();
  }

  protected <T> T major(T x, T y) {
    return axis == Axis.X ? x : y;
  }

  protected <T> T cross(T x, T y) {
    return axis == Axis.X ? y : x;
  }

  public Node spacer(Object id, Dim length) {
    return new Node(
      this, id,
      axis == Axis.X
      ? size(length, px(0))
      : size(px(0), length),
      getElevation()
    );
  }
}
