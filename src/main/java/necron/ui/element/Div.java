package necron.ui.element;

import lombok.Getter;
import lombok.val;
import necron.ui.NecronUi;
import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.LayoutEvent;
import necron.ui.event.RenderEvent;
import necron.ui.layout.Box;
import necron.ui.layout.Dim;
import necron.ui.layout.Direction;
import necron.ui.react.CalcReact;
import necron.ui.react.React;
import necron.ui.render.DebugRect;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static necron.ui.layout.Box.size;
import static necron.ui.layout.Dim.px;
import static yqloss.E._cast;

public class Div extends Node implements Container {
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

  public Div(
    Container parent,
    Direction direction,
    Box.SizePadding sizePadding,
    React<Float> alignment,
    React<Float> elevation
  ) {
    if (sizePadding.width() instanceof Dim.Min) sizePadding = sizePadding.padWidth();
    if (sizePadding.height() instanceof Dim.Min) sizePadding = sizePadding.padHeight();
    widthIndependentOnChildren = sizePadding.width().isIndependentOnChildren();
    heightIndependentOnChildren = sizePadding.height().isIndependentOnChildren();
    addWidthCallbacks = new ArrayList<>();
    addHeightCallbacks = new ArrayList<>();
    removeWidthCallbacks = new ArrayList<>();
    removeHeightCallbacks = new ArrayList<>();
    super(parent, sizePadding.asSize(), elevation);
    this.direction = direction;
    paddingTop = sizePadding.paddingTop();
    paddingRight = sizePadding.paddingRight();
    paddingBottom = sizePadding.paddingBottom();
    paddingLeft = sizePadding.paddingLeft();
    widthWithPadding = React.react(
      () -> getWidth().peek() - paddingLeft.peek() - paddingRight.peek(),
      getWidth(), paddingLeft, paddingRight
    );
    heightWithPadding = React.react(
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

  public static Div x(
    Container parent,
    Box.SizePadding sizePadding,
    React<Float> alignment,
    React<Float> elevation
  ) {
    return new Div(parent, Direction.HORIZONTAL, sizePadding, alignment, elevation);
  }

  public static Div y(
    Container parent,
    Box.SizePadding sizePadding,
    React<Float> alignment,
    React<Float> elevation
  ) {
    return new Div(parent, Direction.VERTICAL, sizePadding, alignment, elevation);
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
  public final Div add(Function<? super Div, ? extends Element>... constructors) {
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
        updateMetrics(context, event, handled);
        for (val child : children()) {
          child.dispatch(context, event, false);
        }
        updateMetrics(context, event, handled);
        return false;
      }

      case RenderEvent renderEvent -> {
        super.dispatch(context, event, handled);
        val innerWidth = getInnerWidth().peek();
        val innerHeight = getInnerHeight().peek();
        val x = paddingLeft.peek();
        val y = paddingTop.peek();
        if (NecronUi.isDebugMode()) {
          renderEvent.yieldRenderable().accept(new DebugRect(
            new Vector2f(x, y),
            new Vector2f(x + innerWidth, y + innerHeight),
            getElevation().peek(),
            1F
          ));
        }
        val crossSpace = cross(innerWidth, innerHeight);
        var accumulated = 0F;
        for (val child : children()) {
          val majorSize = major(child.getWidth(), child.getHeight()).peek();
          val crossSize = cross(child.getWidth(), child.getHeight()).peek();
          val major = accumulated;
          val cross = (crossSpace - crossSize) * alignment.peek();
          val childX = x + major(major, cross);
          val childY = y + cross(major, cross);
          val childEvent = new RenderEvent(
            renderable -> renderEvent.yieldRenderable().accept(
              renderable.translate(new Vector2f(childX, childY))
            )
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

  public Node spacer(Dim length) {
    return new Node(
      this,
      direction == Direction.HORIZONTAL
      ? size(length, px(0))
      : size(px(0), length),
      getElevation()
    );
  }
}
