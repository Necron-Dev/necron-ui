package necron.ui.layout;

import lombok.Value;
import necron.ui.react.React;

import static necron.ui.layout.Dim.fp;

public interface Pos {
  default void update() {}

  enum Auto implements Pos {INSTANCE}

  @Value
  class Anchor implements Pos {
    React<Float> relativeX, relativeY, anchorX, anchorY, offsetX, offsetY;

    @Override
    public void update() {
      relativeX.get();
      relativeY.get();
      anchorX.get();
      anchorY.get();
      offsetX.get();
      offsetY.get();
    }
  }

  static Auto auto() {
    return Auto.INSTANCE;
  }

  static Anchor anchor(
    React<Float> relativeX,
    React<Float> relativeY,
    React<Float> anchorX,
    React<Float> anchorY,
    React<Float> offsetX,
    React<Float> offsetY
  ) {
    return new Anchor(relativeX, relativeY, anchorX, anchorY, offsetX, offsetY);
  }

  static Anchor anchor(
    float relativeX,
    float relativeY,
    float anchorX,
    float anchorY,
    float offsetX,
    float offsetY
  ) {
    return anchor(
      fp(relativeX),
      fp(relativeY),
      fp(anchorX),
      fp(anchorY),
      fp(offsetX),
      fp(offsetY)
    );
  }
}
