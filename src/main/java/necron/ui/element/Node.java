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
import necron.ui.react.React;
import necron.ui.render.DebugRect;
import org.joml.Vector2f;

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

      case RenderEvent renderEvent -> {
        if (NecronUi.isDebugMode()) {
          renderEvent.yieldRenderable().accept(new DebugRect(
            new Vector2f(),
            new Vector2f(getWidth().peek(), getHeight().peek()),
            getElevation().peek(),
            1F
          ));
        }
      }

      default -> {}
    }

    return false;
  }
}
