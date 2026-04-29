import lombok.val;
import moe.nec.ui.context.Context;
import moe.nec.ui.element.Div;
import moe.nec.ui.element.Node;
import moe.nec.ui.event.DebugEvent;
import moe.nec.ui.event.LayoutEvent;
import moe.nec.ui.layout.Align;
import moe.nec.ui.layout.Direction;
import org.joml.Vector2f;
import org.joml.Vector4fc;

import static moe.nec.ui.layout.Box.box;
import static moe.nec.ui.layout.Box.size;
import static moe.nec.ui.layout.Dim.*;

void main() {
  val container = new Div(
    null,
    Direction.HORIZONTAL,
    box(px(1920), px(1080), 100),
    Align.center(),
    fp(0)
  );

  container.add(
    parent -> new Node(
      parent,
      size(px(400), px(200)),
      parent.elevated(1)
    ),
    parent -> new Node(
      parent,
      size(flex(1, 3), flex()),
      fp(0)
    ),
    parent -> new Node(
      parent,
      size(flex(1, 3), flex()),
      fp(0)
    ),
    parent -> new Node(
      parent,
      size(flex(1, 3), flex()),
      fp(0)
    )
  );

  val rects = new ArrayList<Vector4fc>();

  container.dispatch(new Context(), new LayoutEvent(), false);
  container.dispatch(new Context(), new DebugEvent(new Vector2f(0F, 0F), rects::add), false);

  val sb = new StringBuilder();
  for (val rect : rects) {
    sb.append("polygon((");
    sb.append(rect.x());
    sb.append(",");
    sb.append(-rect.y());
    sb.append("),(");
    sb.append(rect.x() + rect.z());
    sb.append(",");
    sb.append(-rect.y());
    sb.append("),(");
    sb.append(rect.x() + rect.z());
    sb.append(",");
    sb.append(-rect.y() - rect.w());
    sb.append("),(");
    sb.append(rect.x());
    sb.append(",");
    sb.append(-rect.y() - rect.w());
    sb.append(")),");
  }
  sb.deleteCharAt(sb.length() - 1);

  System.out.println(sb);
}
