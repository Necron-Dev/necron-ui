package necron.ui.widget.container;

import necron.ui.Surface;
import necron.ui.layout.Axis;
import necron.ui.widget.ChildrenConfiguration;

import static necron.ui.layout.Dim.fp;
import static necron.ui.layout.Dim.px;
import static necron.ui.layout.Padding.padding;
import static necron.ui.layout.Pos.auto;

public class Fullscreen extends Div {
  public Fullscreen(ChildrenConfiguration children) {
    super(
      null,
      Fullscreen.class,
      px(Surface.WIDTH),
      px(Surface.HEIGHT),
      padding(),
      auto(),
      auto(),
      fp(0),
      Axis.Y,
      fp(0),
      children
    );
  }
}
