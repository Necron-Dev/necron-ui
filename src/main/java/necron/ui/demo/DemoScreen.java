package necron.ui.demo;

import com.mojang.brigadier.Command;
import lombok.val;
import necron.ui.NecronUiScreen;
import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.KeyEvent;
import necron.ui.style.DefaultPalettes;
import necron.ui.style.Palette;
import necron.ui.widget.container.Card;
import necron.ui.widget.container.Fullscreen;
import necron.ui.widget.container.SimpleButton;
import necron.ui.widget.element.Text;
import net.minecraft.network.chat.Component;

import static necron.ui.layout.Box.box;
import static necron.ui.layout.Dim.*;
import static necron.ui.layout.Pos.anchor;
import static necron.ui.react.React.useConst;
import static yqloss.E._id;

public class DemoScreen {
  private static Palette a = DefaultPalettes.DARK, b = DefaultPalettes.LIGHT;

  public static int display() {
    NecronUiScreen.display(new Fullscreen(_ -> d0 -> {
      d0.add(
        _id, Card::card,
        it -> it.positioning(anchor(0.5F, 0.5F, 0.5F, 0.5F, 0, 0))
                .sizePadding(box(px(240), px(180), 12))
                .build(),
        _ -> d1 -> {
          d1.add(
            _id, SimpleButton::simpleButton,
            it -> it
                    .sizePadding(box(flex(), min(), 8))
                    .build(),
            _ -> d2 -> {
              d2.add(
                _id, Text::text,
                it -> it.content(useConst(Component.literal("Click")))
                        .color(Palette.GLOBAL.getPrimaryForeground())
                        .build()
              );
            }
          );
          d1.spacer(_id, px(4));
          d1.divider(_id);
          d1.spacer(_id, px(4));
          d1.add(_id, Text::text, it -> it.content(useConst(Component.literal("Hello"))).build());
        }
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
