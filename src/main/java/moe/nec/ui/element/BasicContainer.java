package moe.nec.ui.element;

import lombok.Getter;
import lombok.val;
import moe.nec.ui.context.Context;
import moe.nec.ui.event.DebugEvent;
import moe.nec.ui.event.Event;
import moe.nec.ui.event.LayoutEvent;
import moe.nec.ui.layout.Box;
import moe.nec.ui.layout.Dim;
import moe.nec.ui.layout.Direction;
import moe.nec.ui.react.CalcReact;
import moe.nec.ui.react.React;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static yqloss.E._cast;

public class BasicContainer extends BasicElement implements Container {
  protected final List<Element> children = new ArrayList<>();
  private final boolean widthIndependentOnChildren;
  private final boolean heightIndependentOnChildren;
  private final List<Consumer<React<Float>[]>> addWidthCallbacks;
  private final List<Consumer<React<Float>[]>> addHeightCallbacks;
  private final List<Consumer<React<Float>[]>> removeWidthCallbacks;
  private final List<Consumer<React<Float>[]>> removeHeightCallbacks;
  private final Direction direction;
  private final React<Float> paddingTop;
  private final React<Float> paddingRight;
  private final React<Float> paddingBottom;
  private final React<Float> paddingLeft;
  private final React<Float> widthWithPadding;
  private final React<Float> heightWithPadding;

  @Getter
  private final React<Float> alignment;

  @Getter
  private final React<Float> horizontalSpace;

  @Getter
  private final React<Float> verticalSpace;

  private static class SpaceReact extends CalcReact<Float> {
    private final React<Float> length;
    private final List<React<Float>> reacts;

    public SpaceReact(React<Float> length) {
      this.length = length;
      reacts = new ArrayList<>();
      super(true);
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

  public BasicContainer(
    Container parent,
    Direction direction,
    Box.SizePadding sizePadding,
    React<Float> alignment
  ) {
    if (sizePadding.width() instanceof Dim.Min) sizePadding = sizePadding.padWidth();
    if (sizePadding.height() instanceof Dim.Min) sizePadding = sizePadding.padHeight();
    widthIndependentOnChildren = sizePadding.width().isIndependentOnChildren();
    heightIndependentOnChildren = sizePadding.height().isIndependentOnChildren();
    addWidthCallbacks = new ArrayList<>();
    addHeightCallbacks = new ArrayList<>();
    removeWidthCallbacks = new ArrayList<>();
    removeHeightCallbacks = new ArrayList<>();
    super(parent, sizePadding.asSize());
    this.direction = direction;
    paddingTop = sizePadding.paddingTop();
    paddingRight = sizePadding.paddingRight();
    paddingBottom = sizePadding.paddingBottom();
    paddingLeft = sizePadding.paddingLeft();
    widthWithPadding = React.from(
      () -> getWidth().peek() - paddingLeft.peek() - paddingRight.peek(),
      getWidth(), paddingLeft, paddingRight
    );
    heightWithPadding = React.from(
      () -> getHeight().peek() - paddingTop.peek() - paddingBottom.peek(),
      getHeight(), paddingTop, paddingBottom
    );
    horizontalSpace = direction == Direction.HORIZONTAL
                      ? new SpaceReact(getInnerWidth())
                      : getInnerWidth();
    verticalSpace = direction == Direction.VERTICAL
                    ? new SpaceReact(getInnerHeight())
                    : getInnerHeight();
    this.alignment = alignment;
  }

  public React<Float> getInnerWidth() {
    return widthWithPadding;
  }

  public React<Float> getInnerHeight() {
    return heightWithPadding;
  }

  @Override
  public List<Element> children() {
    return Collections.unmodifiableList(children);
  }

  @Override
  protected React<Float> handleCreateResult(boolean vertical, Dim.CreateResult result) {
    val addCallbacks = vertical ? addHeightCallbacks : addWidthCallbacks;
    val removeCallbacks = vertical ? removeHeightCallbacks : removeWidthCallbacks;
    if (result.addReacts() != null) addCallbacks.add(result.addReacts());
    if (result.removeReacts() != null) removeCallbacks.add(result.removeReacts());
    return result.react();
  }

  @SafeVarargs
  public final BasicContainer add(Function<BasicContainer, Element>... constructors) {
    val widths = new ArrayList<React<Float>>(constructors.length);
    val heights = new ArrayList<React<Float>>(constructors.length);
    val independentWidths = new ArrayList<React<Float>>(constructors.length);
    val independentHeights = new ArrayList<React<Float>>(constructors.length);
    for (val constructor : constructors) {
      val element = constructor.apply(this);
      if (element.isWidthIndependent() || widthIndependentOnChildren)
        widths.add(element.getWidth());
      if (element.isHeightIndependent() || heightIndependentOnChildren)
        heights.add(element.getHeight());
      if (element.isWidthIndependent())
        independentWidths.add(element.getWidth());
      if (element.isHeightIndependent())
        independentHeights.add(element.getHeight());
      children.add(element);
    }
    val widthArray = widths.toArray(new React[0]);
    val heightArray = heights.toArray(new React[0]);
    for (val callback : addWidthCallbacks) {
      callback.accept(_cast(widthArray));
    }
    for (val callback : addHeightCallbacks) {
      callback.accept(_cast(heightArray));
    }
    if (horizontalSpace instanceof SpaceReact space) {
      space.add(_cast(independentWidths.toArray(new React[0])));
    }
    if (verticalSpace instanceof SpaceReact space) {
      space.add(_cast(independentHeights.toArray(new React[0])));
    }
    return this;
  }

  @Override
  public boolean dispatch(Context context, Event event, boolean handled) {
    switch (event) {
      case LayoutEvent _ -> {
        for (var i = 0; i < 2; i++) {
          updateMetrics(context, event, handled);
          for (val child : children()) {
            child.dispatch(context, event, false);
          }
        }
        updateMetrics(context, event, handled);
        return false;
      }

      case DebugEvent debugEvent -> {
        super.dispatch(context, event, handled);
        val innerWidth = getInnerWidth().peek();
        val innerHeight = getInnerHeight().peek();
        val leftPadding = paddingLeft.peek();
        val topPadding = paddingTop.peek();
        val x = debugEvent.position().x() + leftPadding;
        val y = debugEvent.position().y() + topPadding;
        debugEvent.emitRectangle().accept(new Vector4f(
          x,
          y,
          innerWidth,
          innerHeight
        ));
        val crossSpace = cross(innerWidth, innerHeight);
        var accumulated = 0F;
        for (val child : children()) {
          val majorSize = major(child.getWidth(), child.getHeight()).peek();
          val crossSize = cross(child.getWidth(), child.getHeight()).peek();
          val major = accumulated;
          val cross = (crossSpace - crossSize) * alignment.peek();
          val childX = x + major(major, cross);
          val childY = y + cross(major, cross);
          val childEvent = new DebugEvent(
            new Vector2f(0F, 0F),
            rect -> {
              debugEvent.emitRectangle().accept(new Vector4f(
                childX + rect.x(),
                childY + rect.y(),
                rect.z(),
                rect.w()
              ));
            }
          );
          child.dispatch(context, childEvent, false);
          accumulated += majorSize;
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
}
