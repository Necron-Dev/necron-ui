package necron.ui;

import lombok.val;
import necron.ui.animation.Animation;
import necron.ui.animation.Ease;
import necron.ui.context.Context;
import necron.ui.element.Div;
import necron.ui.element.Node;
import necron.ui.event.ContentEvent;
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
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import static necron.ui.layout.Box.box;
import static necron.ui.layout.Box.size;
import static necron.ui.layout.Dim.fp;
import static necron.ui.layout.Dim.px;
import static necron.ui.react.React.listen;
import static necron.ui.react.React.react;
import static necron.ui.util.fn.Fn1.fn;
import static yqloss.E.*;

public class TestInit implements ClientModInitializer {
  private final CalcReact<Float>
    windowWidth = react(() -> (float) $($(Lazy.MC.getWindow().getGuiScaledWidth()), 0)),
    windowHeight = react(() -> (float) $($(Lazy.MC.getWindow().getGuiScaledHeight()), 0));

  private final Animation animation = new Animation(10F);

  private float a = 10F, b = 100F;

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

  private final Div div = Div.x(
    null, _id,
    box(px(windowWidth), px(windowHeight), 100),
    fp(0), Align.center(),
    ctx -> {
      ctx.configure = react -> {
        react.dependsOn(timer);
      };
      return dsl -> {
        if (new Random().nextBoolean()) {
          dsl.add(1, (p, k) -> new Node(p, k, size(px(100), px(100)), p.up(0)));
          dsl.add(2, (p, k) -> new Node(p, k, size(px(100), px(animation)), p.up(0)));
        } else {
          dsl.add(2, (p, k) -> new Node(p, k, size(px(100), px(animation)), p.up(0)));
          dsl.add(1, (p, k) -> new Node(p, k, size(px(100), px(100)), p.up(0)));
        }
      };
    }
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
    val time = System.nanoTime();
    Timestamp.update();
    windowWidth.forceUpdate();
    windowHeight.forceUpdate();
    val ctx = new Context();
    div.dispatch(ctx, ContentEvent.INSTANCE, false);
    div.dispatch(ctx, LayoutEvent.INSTANCE, false);
    div.dispatch(ctx, LayoutEvent.INSTANCE, false);
    div.dispatch(ctx, LayoutEvent.INSTANCE, false);
    val renderables = new ArrayList<Renderable>();
    div.dispatch(ctx, new RenderEvent(renderables::add), false);
    renderables.sort(Comparator.comparing(Renderable::getElevation));
    for (val renderable : renderables) {
      renderable.render(context, deltaTracker);
    }
    val diff = System.nanoTime() - time;
    Lazy.MC.gui.getChat().addMessage(Component.literal(String.format("%012d", diff)));
  }
}
