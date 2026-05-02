package necron.ui.demo;

import com.mojang.brigadier.Command;
import necron.ui.NecronUiScreen;
import necron.ui.style.Palette;
import necron.ui.widget.container.Fullscreen;
import necron.ui.widget.element.RoundedRect;

import static necron.ui.layout.Box.size;
import static necron.ui.layout.Dim.fp;
import static necron.ui.layout.Dim.px;
import static necron.ui.layout.Pos.anchor;
import static yqloss.E._id;

public class DemoScreen {
  public static int display() {
    NecronUiScreen.display(new Fullscreen(_ -> d0 -> {
      d0.add(
        _id, RoundedRect::roundedRect,
        it -> it.positioning(anchor(0.5F, 0.5F, 0.5F, 0.5F, 0, 0))
                .size(size(px(240), px(180)))
                .color(Palette.GLOBAL.getBackground())
                .radius(fp(12))
                .build()
      );
    }));
    return Command.SINGLE_SUCCESS;
  }
}
