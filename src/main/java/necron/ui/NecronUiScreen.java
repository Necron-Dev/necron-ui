package necron.ui;

import lombok.val;
import necron.ui.context.Context;
import necron.ui.event.*;
import necron.ui.render.Renderable;
import necron.ui.widget.Container;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;

public class NecronUiScreen extends Screen {
  private final Container root;
  private final Context context = new Context();

  protected NecronUiScreen(Component title, Container root) {
    super(title);
    this.root = root;
  }

  @Override
  public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
    root.dispatch(context, PreEvent.INSTANCE, false);
    root.dispatch(context, ContentEvent.INSTANCE, false);
    root.dispatch(context, MetricsEvent.INSTANCE, false);
    root.dispatch(context, MetricsEvent.INSTANCE, false);
    root.dispatch(context, MetricsEvent.INSTANCE, false);
    root.dispatch(context, PositionEvent.INSTANCE, false);
    root.dispatch(context, UpdateEvent.INSTANCE, false);
    root.dispatch(context, PostEvent.INSTANCE, false);
    val renderables = new ArrayList<Renderable>();
    root.dispatch(context, new RenderEvent(renderables::add), false);
    renderables.sort(Comparator.comparing(Renderable::getElevation));
    for (val renderable : renderables) {
      renderable.render(gui);
    }
  }

  public static void display(Container root) {
    Lazy.MC.schedule(() -> {
      Lazy.MC.setScreen(new NecronUiScreen(Component.literal("Necron UI"), root));
    });
  }
}
