import lombok.val;
import moe.nec.ui.context.Context;
import moe.nec.ui.element.BasicContainer;
import moe.nec.ui.element.BasicElement;
import moe.nec.ui.event.DebugEvent;
import moe.nec.ui.event.LayoutEvent;
import moe.nec.ui.layout.Align;
import moe.nec.ui.layout.Box;
import moe.nec.ui.layout.Dim;
import moe.nec.ui.layout.Direction;
import org.joml.Vector2f;
import org.joml.Vector4fc;

void main() {
  val container = new BasicContainer(
    null,
    Direction.HORIZONTAL,
    Box.box(Dim.px(1920), Dim.px(1080), 100),
    Align.center()
  );

  container.add(
    parent -> new BasicElement(
      parent,
      Box.size(Dim.px(400), Dim.px(200))
    ),
    parent -> new BasicElement(
      parent,
      Box.size(Dim.flex(1, 3), Dim.max())
    ),
    parent -> new BasicElement(
      parent,
      Box.size(Dim.flex(1, 3), Dim.max())
    ),
    parent -> new BasicElement(
      parent,
      Box.size(Dim.flex(1, 3), Dim.max())
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
