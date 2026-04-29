package necron.ui;

import lombok.val;
import necron.ui.animation.Animation;
import necron.ui.animation.Ease;
import necron.ui.context.Context;
import necron.ui.element.Container;
import necron.ui.element.Div;
import necron.ui.element.Node;
import necron.ui.event.LayoutEvent;
import necron.ui.event.RenderEvent;
import necron.ui.layout.Align;
import necron.ui.react.CalcReact;
import necron.ui.render.Renderable;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Comparator;

import static necron.ui.layout.Box.box;
import static necron.ui.layout.Box.size;
import static necron.ui.layout.Dim.*;
import static necron.ui.react.React.listen;
import static necron.ui.react.React.react;
import static necron.ui.util.fn.Fn1.fn;
import static yqloss.E.$;
import static yqloss.E._also;

public class TestInit implements ClientModInitializer {
  private final CalcReact<Float> windowWidth =
    react(() -> (float) $($(Lazy.MC.getWindow().getGuiScaledWidth()), 0));

  private final CalcReact<Float> windowHeight =
    react(() -> (float) $($(Lazy.MC.getWindow().getGuiScaledHeight()), 0));

  private final Animation animation = new Animation(10F);

  private float a = 10.0F;

  private float b = 100.0F;

  private final CalcReact<Long> timer = _also(
    listen(
      fn((Long l) -> l / 2_000_000_000L),
      Timestamp.NANO_TIME
    ),
    x -> x.hooking((_, _) -> {
      animation.next(Ease.ELASTIC.out(), b, 0, 1000);
      val tmp = a;
      a = b;
      b = tmp;
    })
  );

  private final Container div =
    Div.x(null, box(px(windowWidth), px(windowHeight), 100), Align.center(), fp(0))
      .add(
        div -> new Node(div, size(flex(1, 3), flex()), div.up(1)),
        div -> div.spacer(px(animation)),
        div -> new Node(div, size(flex(1, 3), flex()), div.up(1)),
        div -> new Node(div, size(flex(1, 3), flex()), div.up(1))
      );

  @Override
  public void onInitializeClient() {
    HudElementRegistry.attachElementAfter(
      VanillaHudElements.CHAT,
      NecronUi.identifier("hud"),
      this::render
    );
  }

  private void render(GuiGraphics context, DeltaTracker deltaTracker) {
    Timestamp.update();
    windowWidth.forceUpdate();
    windowHeight.forceUpdate();
    val ctx = new Context();
    div.dispatch(ctx, LayoutEvent.INSTANCE, false);
    div.dispatch(ctx, LayoutEvent.INSTANCE, false);
    div.dispatch(ctx, LayoutEvent.INSTANCE, false);
    val renderables = new ArrayList<Renderable>();
    div.dispatch(ctx, new RenderEvent(renderables::add), false);
    renderables.sort(Comparator.comparing(Renderable::getElevation));
    for (val renderable : renderables) {
      renderable.render(context, deltaTracker);
    }
  }
}
