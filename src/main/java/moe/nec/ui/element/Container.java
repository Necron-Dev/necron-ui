package moe.nec.ui.element;

import lombok.val;
import moe.nec.ui.context.Context;
import moe.nec.ui.event.Event;
import moe.nec.ui.react.React;
import moe.nec.ui.request.Request;

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
