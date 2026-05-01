package necron.ui;

import lombok.val;
import necron.ui.animation.Animation;
import necron.ui.animation.Ease;
import necron.ui.context.Context;
import necron.ui.element.Div;
import necron.ui.element.Node;
import necron.ui.element.RoundedRect;
import necron.ui.event.*;
import necron.ui.react.CalcReact;
import necron.ui.react.UpdaterReact;
import necron.ui.render.Renderable;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;

import static necron.ui.layout.Axis.X;
import static necron.ui.layout.Box.box;
import static necron.ui.layout.Box.size;
import static necron.ui.layout.Dim.*;
import static necron.ui.layout.Pos.anchor;
import static necron.ui.layout.Pos.auto;
import static necron.ui.react.React.*;
import static yqloss.E.*;

public class TestInit implements ClientModInitializer {
  private final CalcReact<Float>
    windowWidth = useCalc(() -> (float) $($(Lazy.MC.getWindow().getGuiScaledWidth()), 0)),
    windowHeight = useCalc(() -> (float) $($(Lazy.MC.getWindow().getGuiScaledHeight()), 0));

  private final Animation animation = new Animation(200F);
  private final Animation animation2 = new Animation(0F);

  private float a = 0F, b = 200F;

  private final UpdaterReact updater = useUpdater();

  private final CalcReact<Long> timer = _also(
    useListen(Timestamp.NANO_TIME, l -> l / 2_000_000_000L),
    x -> x.hooking((_, _) -> {
//      animation.next(Ease.ELASTIC.out(), b, 0, 1000);
      animation2.next(Ease.EXPO.in(), b, 0, 1000);
      val tmp = a;
      a = b;
      b = tmp;
      updater.update();
    })
  );

  private final Div div = new Div(
    null, _id, box(min(), min(), 100), auto(), fp(0), X, fp(0.5F),
    _ -> dsl -> {
      RoundedRect.background(dsl, animation2, useConst(0xFF111111));
      dsl.add(_id, (p, k) -> new Node(p, k, size(px(50), px(50)), auto(), p.up(1)));
      dsl.add(_id, (p, k) -> new Node(p, k, size(px(50), px(50)), anchor(0.5F, 0.5F, 0.5F, 0.5F, 0, 0), p.up(1)));
      dsl.add(_id, (p, k) -> new Node(p, k, size(px(animation), px(animation)), auto(), p.up(1)));
      dsl.add(_id, (p, k) -> new Node(p, k, size(px(200), flex()), auto(), p.up(1)));
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
    div.dispatch(ctx, MetricsEvent.INSTANCE, false);
    div.dispatch(ctx, MetricsEvent.INSTANCE, false);
    div.dispatch(ctx, MetricsEvent.INSTANCE, false);
    div.dispatch(ctx, PositionEvent.INSTANCE, false);
    div.dispatch(ctx, UpdateEvent.INSTANCE, false);
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
