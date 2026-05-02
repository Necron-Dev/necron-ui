package necron.ui.demo;

import com.mojang.brigadier.Command;
import lombok.val;
import necron.ui.NecronUiScreen;
import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.KeyEvent;
import necron.ui.style.DefaultPalettes;
import necron.ui.style.Palette;
import necron.ui.widget.container.Fullscreen;
import necron.ui.widget.element.RoundedRect;
import necron.ui.widget.element.Text;
import net.minecraft.network.chat.Component;

import static necron.ui.layout.Box.size;
import static necron.ui.layout.Dim.*;
import static necron.ui.layout.Pos.anchor;
import static necron.ui.react.React.useConst;
import static yqloss.E._id;

public class DemoScreen {
  private static Palette a = DefaultPalettes.DARK, b = DefaultPalettes.LIGHT;

  public static int display() {
    NecronUiScreen.display(new Fullscreen(_ -> d0 -> {
      d0.add(
        _id, RoundedRect::roundedRect,
        it ->
          it.positioning(anchor(0.5F, 0.5F, 0.5F, 0.5F, 0, 0))
            .size(size(px(240), px(180)))
            .color(Palette.GLOBAL.getBackground())
            .radius(fp(12))
            .build()
      );
      d0.add(
        _id, Text::text,
        it ->
          it.positioning(anchor(0.5F, 0.5F, 0.5F, 0.5F, 0, 0))
            .size(size(px(240), min()))
            .color(Palette.GLOBAL.getForeground())
            .fontSize(fp(64))
            .elevation(d0.parent().up(1.1F))
            .content(useConst(Component.literal("Hello World")))
            .build()
      );
    }) {
      @Override
      public boolean dispatch(Context context, Event event, boolean handled) {
        if (event instanceof KeyEvent keyEvent) {
          if (keyEvent.getType() == KeyEvent.Type.MOUSE && keyEvent.isPress()) {
            Palette.switchGlobal(b);
            val t = a;
            a = b;
            b = t;
            return true;
          }
        }
        return super.dispatch(context, event, handled);
      }
    });
    return Command.SINGLE_SUCCESS;
  }
}
