package necron.ui;

import lombok.val;
import necron.ui.animation.Animation;
import necron.ui.animation.Ease;
import necron.ui.context.Context;
import necron.ui.element.*;
import necron.ui.event.*;
import necron.ui.react.CalcReact;
import necron.ui.react.React;
import necron.ui.render.Renderable;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;

import static necron.ui.layout.Axis.X;
import static necron.ui.layout.Axis.Y;
import static necron.ui.layout.Box.box;
import static necron.ui.layout.Box.size;
import static necron.ui.layout.Dim.*;
import static necron.ui.layout.Pos.anchor;
import static necron.ui.layout.Pos.auto;
import static necron.ui.react.React.*;
import static yqloss.E.$;
import static yqloss.E._id;

public class TestInit implements ClientModInitializer {
  private final CalcReact<Float>
    windowWidth = useCalc(() -> (float) $($(Lazy.MC.getWindow().getGuiScaledWidth()), 0)),
    windowHeight = useCalc(() -> (float) $($(Lazy.MC.getWindow().getGuiScaledHeight()), 0));

  private final CalcReact<Vector2fc> mousePos = useCalc(() -> {
    val m = Lazy.MC.mouseHandler;
    val w = Lazy.MC.getWindow();
    if (w == null) return new Vector2f();
    return new Vector2f(
      (float) (double) $($(m.getScaledXPos(w)), 0.0),
      (float) (double) $($(m.getScaledYPos(w)), 0.0)
    );
  });

  private final Animation animation = new Animation(200F);
  private float a = 100F, b = 200F;

  private final React<Long> timer =
    Timestamp.timer(fp(2000))
      .hooking((_, _) -> {
        animation.next(Ease.ELASTIC.out(), b, 0, 1000);
        val tmp = a;
        a = b;
        b = tmp;
      });

  private final Div root = new Div(
    null, _id, box(px(windowWidth), px(windowHeight)), auto(), fp(0), Y, fp(0),
    _ -> dsl -> dsl.add(_id, this::createContent)
  );

  private Element createContent(Container p0, Object k0) {
    val isMouseInside = useBox(false);
    val color = useGradient(
      useCalc(isMouseInside, x -> x ? 0xFF003F00 : 0xFF3F0000),
      (a, x) -> a.interrupt().next(Ease.LINEAR, x, 0, 500)
    );

    return new Div(
      p0, k0, box(min(), min(), 100), anchor(0.5F, 0.5F, 0.5F, 0.5F, 0, 0), p0.up(1), X, fp(0.5F),
      _ -> dsl -> {
        RoundedRect.background(dsl, fp(64), color);
        dsl.add(_id, (p1, k1) -> new Node(p1, k1, size(px(50), px(50)), auto(), p1.up(1)));
        dsl.add(_id, (p1, k1) -> new Node(p1, k1, size(px(50), px(50)), anchor(0.5F, 0.5F, 0.5F, 0.5F, 0, 0), p1.up(1)));
        dsl.add(
          _id, (p1, k1) -> new Node(p1, k1, size(px(animation), px(animation)), auto(), p1.up(1)) {
            @Override
            public boolean dispatch(Context context, Event event, boolean handled) {
              switch (event) {
                case MouseMoveEvent e -> {
                  val inside = isInside(e);
                  isMouseInside.set(inside);
                  return inside;
                }

                default -> {}
              }
              return super.dispatch(context, event, handled);
            }
          }
        );
        dsl.add(_id, (p1, k1) -> new Node(p1, k1, size(px(200), flex()), auto(), p1.up(1)));
      }
    );
  }

  @Override
  public void onInitializeClient() {
    HudElementRegistry.attachElementAfter(
      VanillaHudElements.CHAT,
      NecronUi.identifier("hud"),
      this::render
    );
  }

  private void render(GuiGraphics context, DeltaTracker deltaTracker) {
    if (GLFW.glfwGetKey(Lazy.MC.getWindow().handle(), GLFW.GLFW_KEY_DELETE) == GLFW.GLFW_PRESS) {
      return;
    }
    val time = System.nanoTime();
    Timestamp.update();
    timer.get();
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
