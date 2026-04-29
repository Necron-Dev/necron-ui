package necron.ui.event;

import necron.ui.render.Renderable;

import java.util.function.Consumer;

public record RenderEvent(
  Consumer<? super Renderable> yieldRenderable
) implements Event {}
