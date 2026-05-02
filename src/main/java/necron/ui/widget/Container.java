package necron.ui.widget;

import lombok.val;
import necron.ui.context.Context;
import necron.ui.event.ContentEvent;
import necron.ui.event.Event;
import necron.ui.react.ListReact;
import necron.ui.react.React;
import necron.ui.request.Request;
import necron.ui.util.Transformable;
import org.joml.Vector2f;

public interface Container extends Element {
  ListReact<? extends Element> getChildren();

  React<Float> getHorizontalSpace();

  React<Float> getVerticalSpace();

  @Override
  default boolean dispatch(Context context, Event event, boolean handled) {
    switch (event) {
      case ContentEvent _ -> {
        getChildren().get();
      }

      default -> {}
    }

    for (val child : getChildren().peek()) {
      val transformedEvent =
        event instanceof Transformable<?> transformable
        ? transformable.translate(new Vector2f(-child.getX().peek(), -child.getY().peek()))
        : event;
      if (!(transformedEvent instanceof Event eventToDispatch)) continue;
      handled |= child.dispatch(context, eventToDispatch, handled);
    }
    return handled;
  }

  default <T> T request(Request<T> request) {
    return getParent().request(request);
  }
}
