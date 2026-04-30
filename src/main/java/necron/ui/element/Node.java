package necron.ui.element;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import necron.ui.NecronUi;
import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.MetricsEvent;
import necron.ui.event.RenderEvent;
import necron.ui.layout.Box;
import necron.ui.react.React;
import necron.ui.render.DebugRect;
import org.joml.Vector2f;

import static necron.ui.react.React.subList;
import static yqloss.E.$;

@Getter
@AllArgsConstructor
public class Node implements Element {
  private final Container parent;
  private final Object key;
  private final React<Float> width, height, elevation;
  private final boolean widthIndependent, heightIndependent;

  public Node(Container parent, Object key, Box.Size size, React<Float> elevation) {
    this.parent = parent;
    this.key = key;
    val width = size.getWidth();
    val height = size.getHeight();
    this.width = width.create(subList(), $(parent.getHorizontalSpace()), false);
    widthIndependent = width.isIndependent();
    this.height = height.create(subList(), $(parent.getVerticalSpace()), false);
    heightIndependent = height.isIndependent();
    this.elevation = elevation;
  }

  @Override
  public boolean dispatch(Context context, Event event, boolean handled) {
    switch (event) {
      case MetricsEvent _ -> {
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
