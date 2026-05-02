package necron.ui;

import lombok.val;
import necron.ui.context.Context;
import necron.ui.event.*;
import necron.ui.render.Renderable;
import necron.ui.widget.Container;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
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

  @Override
  public boolean keyPressed(KeyEvent event) {
    if (super.keyPressed(event)) return true;
    root.dispatch(
      context, new necron.ui.event.KeyEvent(
        necron.ui.event.KeyEvent.Type.KEYBOARD,
        event.key(),
        true
      ), false
    );
    return true;
  }

  @Override
  public boolean keyReleased(KeyEvent event) {
    if (super.keyReleased(event)) return true;
    root.dispatch(
      context, new necron.ui.event.KeyEvent(
        necron.ui.event.KeyEvent.Type.KEYBOARD,
        event.key(),
        false
      ), false
    );
    return true;
  }

  @Override
  public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
    if (super.mouseClicked(event, isDoubleClick)) return true;
    root.dispatch(
      context, new necron.ui.event.KeyEvent(
        necron.ui.event.KeyEvent.Type.MOUSE,
        event.button(),
        true
      ), false
    );
    return true;
  }

  @Override
  public boolean mouseReleased(MouseButtonEvent event) {
    if (super.mouseReleased(event)) return true;
    root.dispatch(
      context, new necron.ui.event.KeyEvent(
        necron.ui.event.KeyEvent.Type.MOUSE,
        event.button(),
        false
      ), false
    );
    return true;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }

  public static void display(Container root) {
    Lazy.MC.schedule(() -> {
      Lazy.MC.setScreen(new NecronUiScreen(Component.literal("Necron UI"), root));
    });
  }
}
