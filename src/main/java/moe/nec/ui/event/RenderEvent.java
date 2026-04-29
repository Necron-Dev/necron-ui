package moe.nec.ui.event;

import moe.nec.ui.render.Renderable;

import java.util.function.Consumer;

public record RenderEvent(
  Consumer<? extends Renderable> yieldRenderable
) {}
