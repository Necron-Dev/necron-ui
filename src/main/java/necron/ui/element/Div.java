package necron.ui.element;

import lombok.Getter;
import lombok.val;
import necron.ui.NecronUi;
import necron.ui.context.Context;
import necron.ui.event.*;
import necron.ui.layout.Box;
import necron.ui.layout.Dim;
import necron.ui.layout.Direction;
import necron.ui.react.CalcReact;
import necron.ui.react.React;
import necron.ui.react.SubListReact;
import necron.ui.render.DebugRect;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static necron.ui.layout.Box.size;
import static necron.ui.layout.Dim.fp;
import static necron.ui.layout.Dim.px;
import static necron.ui.react.React.*;
import static necron.ui.util.fn.Fn2.fn;
import static necron.ui.util.fn.Fn3.fn;

public class Div extends Node implements Container {
  private final boolean widthIndependentOnChildren, heightIndependentOnChildren;

  private final List<Consumer<React<Float>[]>>
    addWidthCallbacks,
    addHeightCallbacks,
    removeWidthCallbacks,
    removeHeightCallbacks;

  private final Direction direction;

  private final React<Float>
    paddingTop,
    paddingRight,
    paddingBottom,
    paddingLeft,
    widthWithPadding,
    heightWithPadding;

  @Getter
  private final SubListReact<Element> children = subList();

  @Getter
  private final React<Float> alignment, horizontalSpace, verticalSpace;

  private final WeakHashMap<Object, Vector2fc> cachedPositions = new WeakHashMap<>();

  private static class SpaceReact extends CalcReact<Float> {
    private final React<Float> length;
    private final List<React<Float>> reacts;

    public SpaceReact(React<Float> length) {
      this.length = length;
      reacts = new ArrayList<>();
      super(Objects::equals);
      dependsOn(length);
    }

    @Override
    protected Float calculate() {
      var size = length.peek();
      for (val react : reacts) {
        size -= react.peek();
      }
      return Math.max(size, 0F);
    }

    @SafeVarargs
    public final void add(React<Float>... reacts) {
      Collections.addAll(this.reacts, reacts);
      dependsOn(reacts);
      forceUpdate();
    }

    @SafeVarargs
    public final void remove(React<Float>... reacts) {
      this.reacts.removeAll(Arrays.asList(reacts));
      cancelDependencies(reacts);
      forceUpdate();
    }
  }

  private React<Float> createSpaceReact(
    React<Float> length,
    boolean major,
    Function<? super Element, ? extends React<Float>> elementLength,
    Predicate<? super Element> elementIndependent
  ) {
    if (!major) return length;
    val childrenLength = subList(
      children,
      x -> elementIndependent.test(x)
           ? elementLength.apply(x)
           : fp(0)
    );
    val spaceResult = react(
      fn((Float lengthValue, List<React<Float>> listReact) -> {
        var space = lengthValue;
        for (val element : listReact) {
          space -= element.peek();
        }
        return space;
      }),
      length, childrenLength
    );
    listDepend(spaceResult, childrenLength);
    return spaceResult;
  }

  public Div(
    Container parent,
    Object key,
    Box.SizePadding sizePadding,
    React<Float> elevation,
    Direction direction,
    React<Float> alignment,
    ChildrenConfiguration children
  ) {
    if (sizePadding.getWidth() instanceof Dim.Min) sizePadding = sizePadding.padWidth();
    if (sizePadding.getHeight() instanceof Dim.Min) sizePadding = sizePadding.padHeight();
    widthIndependentOnChildren = sizePadding.getWidth().isIndependentOnChildren();
    heightIndependentOnChildren = sizePadding.getHeight().isIndependentOnChildren();
    addWidthCallbacks = new ArrayList<>();
    addHeightCallbacks = new ArrayList<>();
    removeWidthCallbacks = new ArrayList<>();
    removeHeightCallbacks = new ArrayList<>();
    this.direction = direction;
    paddingTop = sizePadding.getPaddingTop();
    paddingRight = sizePadding.getPaddingRight();
    paddingBottom = sizePadding.getPaddingBottom();
    paddingLeft = sizePadding.getPaddingLeft();
    this.alignment = alignment;
    super(parent, key, sizePadding.asSize(), elevation);
    widthWithPadding = react(
      fn((Float w, Float l, Float r) -> w - l - r),
      getWidth(), paddingLeft, paddingRight
    );
    heightWithPadding = react(
      fn((Float h, Float t, Float b) -> h - t - b),
      getHeight(), paddingTop, paddingBottom
    );
    horizontalSpace = createSpaceReact(
      getInnerWidth(),
      direction == Direction.HORIZONTAL,
      Element::getWidth,
      Element::isWidthIndependent
    );
    verticalSpace = createSpaceReact(
      getInnerHeight(),
      direction == Direction.VERTICAL,
      Element::getHeight,
      Element::isHeightIndependent
    );
    this.children.setParent(ChildrenConfiguration.buildChildren(this, children), x -> x);
  }

  public static Div x(
    Container parent,
    Object key,
    Box.SizePadding sizePadding,
    React<Float> elevation,
    React<Float> alignment,
    ChildrenConfiguration children
  ) {
    return new Div(parent, key, sizePadding, elevation, Direction.HORIZONTAL, alignment, children);
  }

  public static Div y(
    Container parent,
    Object key,
    Box.SizePadding sizePadding,
    React<Float> elevation,
    React<Float> alignment,
    ChildrenConfiguration children
  ) {
    return new Div(parent, key, sizePadding, elevation, Direction.VERTICAL, alignment, children);
  }

  public React<Float> getInnerWidth() {
    return widthWithPadding;
  }

  public React<Float> getInnerHeight() {
    return heightWithPadding;
  }

  @Override
  protected React<Float> handleCreateResult(boolean vertical, Dim.CreateResult result) {
    val addCallbacks = vertical ? addHeightCallbacks : addWidthCallbacks;
    val removeCallbacks = vertical ? removeHeightCallbacks : removeWidthCallbacks;
    if (result.getAddReacts() != null) addCallbacks.add(result.getAddReacts());
    if (result.getRemoveReacts() != null) removeCallbacks.add(result.getRemoveReacts());
    return result.getReact();
  }

  //  @SafeVarargs
//  public final Div add(Function<? super Div, ? extends Element>... constructors) {
//    val widths = new ArrayList<React<Float>>(constructors.length);
//    val heights = new ArrayList<React<Float>>(constructors.length);
//    val independentWidths = new ArrayList<React<Float>>(constructors.length);
//    val independentHeights = new ArrayList<React<Float>>(constructors.length);
//    for (val constructor : constructors) {
//      val element = constructor.apply(this);
//      if (element.isWidthIndependent() || widthIndependentOnChildren)
//        widths.add(element.getWidth());
//      if (element.isHeightIndependent() || heightIndependentOnChildren)
//        heights.add(element.getHeight());
//    }
//    val widthArray = widths.toArray(new React[0]);
//    val heightArray = heights.toArray(new React[0]);
//    for (val callback : addWidthCallbacks) {
//      callback.accept(_cast(widthArray));
//    }
//    for (val callback : addHeightCallbacks) {
//      callback.accept(_cast(heightArray));
//    }
//    return this;
//  }
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
    return direction == Direction.HORIZONTAL ? x : y;
  }

  protected <T> T cross(T x, T y) {
    return direction == Direction.HORIZONTAL ? y : x;
  }

  public Node spacer(Object id, Dim length) {
    return new Node(
      this, id,
      direction == Direction.HORIZONTAL
      ? size(length, px(0))
      : size(px(0), length),
      getElevation()
    );
  }
}
