package moe.nec.ui.render;

import org.joml.Vector2fc;

public interface Renderable {
  float getElevation();

  void render();

  interface Translatable extends Renderable {
    Renderable translate(Vector2fc vec);
  }

  interface Scalable extends Translatable {
    Renderable scale(Vector2fc origin, float factor);

    default Float getElevationScaleFactor() {
      return 0.005F * getElevation();
    }
  }

  interface Opacifiable extends Renderable {
    Renderable opacify(float factor);
  }

  interface Shadowable extends Renderable {
    Renderable shadow(float elevationBelow);
  }
}
