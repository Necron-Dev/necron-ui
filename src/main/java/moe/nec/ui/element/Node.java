package moe.nec.ui.element;

import lombok.Getter;
import lombok.val;
import moe.nec.ui.context.Context;
import moe.nec.ui.event.DebugEvent;
import moe.nec.ui.event.Event;
import moe.nec.ui.event.LayoutEvent;
import moe.nec.ui.layout.Box;
import moe.nec.ui.layout.Dim;
import moe.nec.ui.react.React;
import org.joml.Vector4f;

import static yqloss.E.$;

public class Node implements Element {
  @Getter
  private final Container parent;

  @Getter
  private final React<Float> width;

  @Getter
  private final boolean widthIndependent;

  @Getter
  private final React<Float> height;

  @Getter
  private final boolean heightIndependent;

  @Getter
  private final React<Float> elevation;

  public Node(Container parent, Box.Size size, React<Float> elevation) {
    val width = size.width();
    val height = size.height();
    this.parent = parent;
    val widthResult = width.create($(parent.getHorizontalSpace()), false);
    val heightResult = height.create($(parent.getVerticalSpace()), false);
    this.width = handleCreateResult(false, widthResult);
    widthIndependent = width.isIndependent();
    this.height = handleCreateResult(true, heightResult);
    heightIndependent = height.isIndependent();
    this.elevation = elevation;
  }

  protected React<Float> handleCreateResult(boolean vertical, Dim.CreateResult result) {
    return result.react();
  }

  @Override
  public boolean dispatch(Context context, Event event, boolean handled) {
    switch (event) {
      case LayoutEvent _ -> {
        width.get();
        height.get();
        elevation.get();
      }

      case DebugEvent debugEvent -> {
        debugEvent.emitRectangle().accept(new Vector4f(
          debugEvent.position().x(),
          debugEvent.position().y(),
          getWidth().peek(),
          getHeight().peek()
        ));
      }

      default -> {}
    }

    return false;
  }
}
