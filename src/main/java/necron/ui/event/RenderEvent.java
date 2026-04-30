package necron.ui.event;

import lombok.Value;
import necron.ui.render.Renderable;

import java.util.function.Consumer;

@Value
public class RenderEvent implements Event {
  Consumer<? super Renderable> yieldRenderable;
}
