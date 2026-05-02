package necron.ui;

import lombok.val;
import necron.ui.animation.Ease;
import necron.ui.context.Context;
import necron.ui.event.*;
import necron.ui.react.CalcReact;
import necron.ui.render.Renderable;
import necron.ui.widget.Container;
import necron.ui.widget.Element;
import necron.ui.widget.container.Card;
import necron.ui.widget.container.Div;
import necron.ui.widget.element.Text;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;

import static necron.ui.layout.Axis.Y;
import static necron.ui.layout.Box.box;
import static necron.ui.layout.Dim.*;
import static necron.ui.layout.Pos.anchor;
import static necron.ui.react.React.*;
import static yqloss.E.$;
import static yqloss.E._id;

public class TestUi {
  private static final CalcReact<Float>
    windowWidth = useCalc(() -> (float) $($(Lazy.MC.getWindow().getGuiScaledWidth()), 0)),
    windowHeight = useCalc(() -> (float) $($(Lazy.MC.getWindow().getGuiScaledHeight()), 0));

  private static final CalcReact<Vector2fc> mousePos = useCalc(() -> {
    val m = Lazy.MC.mouseHandler;
    val w = Lazy.MC.getWindow();
    if (w == null) return new Vector2f();
    return new Vector2f(
      (float) (double) $($(m.getScaledXPos(w)), 0.0),
      (float) (double) $($(m.getScaledYPos(w)), 0.0)
    );
  });

  private static final Div root =
    Div.div(null, _id)
      .sizePadding(box(px(windowWidth), px(windowHeight)))
      .children(_ -> dsl -> dsl.add(_id, TestUi::createScaled))
      .build();

  private static Element createScaled(Container p0, Object k0) {
    val isMouseInside = useBox(false);
    val width = useAnimated(
      useCalc(isMouseInside, x -> x ? 300F : 150F),
      (a, x) -> a.interrupt().next(Ease.EXPO.out(), x, 0, 500)
    );
    val height = useAnimated(
      useCalc(isMouseInside, x -> x ? 150F : 300F),
      (a, x) -> a.interrupt().next(Ease.EXPO.out(), x, 0, 500)
    );
    return new Card(
      p0, k0, box(px(width), min(), 16), anchor(0.5F, 0.5F, 0.5F, 0.5F, 0, 0), p0.up(1), Y, fp(0.5F),
      fp(16), useConst(0xFF111111),
      _ -> d0 -> {
        d0.add(
          _id, Text::text,
          x ->
            x.content(useConst(Component.literal("aaron abduct accidental adjacent aeron afghan after agnostic aint anaheim and anybody although allotted ambidextrous amend announced aqua arrangement ask aspin ate attitude authorised avant avery award axolotl baby baghdad banana balter batty baste beeped bent binded billow bizarre blood bottle bowwow boyfriend builder bubbly buzzing byproduct bystander byzantine cappuccino cattle cent chant cicero close coccyx cooked copyright coyote")))
              .build()
        );
      }
    ) {
      @Override
      public boolean dispatch(Context context, Event event, boolean handled) {
        switch (event) {
          case MetricsEvent _ -> {
            super.dispatch(context, event, handled);
            return false;
          }

          case MouseMoveEvent e -> {
            val inside = isInside(e);
            isMouseInside.set(inside);
            return inside | super.dispatch(context, event, handled);
          }

          default -> {}
        }
        return super.dispatch(context, event, handled);
      }
    };
  }

  public static void render(GuiGraphics context, DeltaTracker deltaTracker) {
    if (GLFW.glfwGetKey(Lazy.MC.getWindow().handle(), GLFW.GLFW_KEY_DELETE) == GLFW.GLFW_PRESS) {
      return;
    }
    val time = System.nanoTime();
    Timestamp.update();
    windowWidth.forceUpdate();
    windowHeight.forceUpdate();
    mousePos.forceUpdate();
    val ctx = new Context();
    root.dispatch(ctx, ContentEvent.INSTANCE, false);
    root.dispatch(ctx, MetricsEvent.INSTANCE, false);
    root.dispatch(ctx, MetricsEvent.INSTANCE, false);
    root.dispatch(ctx, MetricsEvent.INSTANCE, false);
    root.dispatch(ctx, PositionEvent.INSTANCE, false);
    root.dispatch(ctx, UpdateEvent.INSTANCE, false);
    root.dispatch(ctx, new MouseMoveEvent(mousePos.peek()), false);
    val renderables = new ArrayList<Renderable>();
    root.dispatch(ctx, new RenderEvent(renderables::add), false);
    renderables.sort(Comparator.comparing(Renderable::getElevation));
    for (val renderable : renderables) {
      renderable.render(context, deltaTracker);
    }
    val diff = System.nanoTime() - time;
    Lazy.MC.gui.getChat().addMessage(Component.literal(String.format("%012d", diff)));
  }
}
