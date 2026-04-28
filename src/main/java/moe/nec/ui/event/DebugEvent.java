package moe.nec.ui.event;

import lombok.With;
import org.joml.Vector2fc;
import org.joml.Vector4fc;

import java.util.function.Consumer;

@With
public record DebugEvent(
  Vector2fc position,
  Consumer<? super Vector4fc> emitRectangle
) implements Event {}
