package necron.ui.element;

import lombok.val;
import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.react.React;
import necron.ui.request.Request;

import java.util.List;

public interface Container extends Element {
  List<Element> children();

  React<Float> getHorizontalSpace();

  React<Float> getVerticalSpace();

  @Override
  default boolean dispatch(Context context, Event event, boolean handled) {
    for (val child : children()) {
      handled |= child.dispatch(context, event, handled);
    }
    return handled;
  }

  default <T> T request(Request<T> request) {
    return getParent().request(request);
  }
}
