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

@Getter
public class Node implements Element {
  private final Object key;
  private final Container parent;
  private final React<Float> width, height, elevation;
  private final boolean widthIndependent, heightIndependent;

  public Node(Container parent, Object key, Box.Size size, React<Float> elevation) {
    this.key = key;
    val width = size.getWidth();
    val height = size.getHeight();
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
    return result.getReact();
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
          renderEvent.getYieldRenderable().accept(new DebugRect(
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
