package necron.ui.event;

import lombok.Value;
import necron.ui.render.Renderable;
import necron.ui.util.Transformable;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.function.Consumer;

@Value
public class RenderEvent implements Event, Transformable<RenderEvent> {
  Consumer<? super Renderable> yieldRenderable;

  @Override
  public RenderEvent translate(Vector2fc vec) {
    return new RenderEvent(x -> yieldRenderable.accept(x.translate(new Vector2f(vec).negate())));
  }

  @Override
  public RenderEvent scale(Vector2fc origin, float factor) {
    return new RenderEvent(x -> yieldRenderable.accept(x.scale(origin, 1F / factor)));
  }
}
