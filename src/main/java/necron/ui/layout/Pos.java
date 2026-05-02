package necron.ui.layout;

import lombok.Value;
import lombok.With;
import necron.ui.react.React;

import static necron.ui.layout.Dim.fp;
import static necron.ui.layout.Pos.anchor;
import static necron.ui.react.React.useCalc;
import static necron.ui.util.fn.Fn2.fn;

public interface Pos {
  default void update() {}

  enum Auto implements Pos {INSTANCE}

  @With
  @Value
  class Anchor implements Pos {
    React<Float> relative, anchor, offset;

    @Override
    public void update() {
      relative.get();
      anchor.get();
      offset.get();
    }

    public Anchor offset(React<Float> offset) {
      return withOffset(useCalc(fn(Float::sum), this.offset, offset));
    }

    public Anchor offset(float offset) {
      return withOffset(useCalc(this.offset, x -> x + offset));
    }
  }

  static Auto auto() {
    return Auto.INSTANCE;
  }

  static Anchor anchor(
    React<Float> relative,
    React<Float> anchor,
    React<Float> offset
  ) {
    return new Anchor(relative, anchor, offset);
  }

  static Anchor anchor(
    float relative,
    float anchor,
    float offset
  ) {
    return anchor(
      fp(relative),
      fp(anchor),
      fp(offset)
    );
  }

  Anchor
    ANCHOR_LL = anchor(0, 0, 0),
    ANCHOR_LC = anchor(0, 0.5F, 0),
    ANCHOR_LR = anchor(0, 1, 0),
    ANCHOR_CL = anchor(0.5F, 0, 0),
    ANCHOR_CC = anchor(0.5F, 0.5F, 0),
    ANCHOR_CR = anchor(0.5F, 1, 0),
    ANCHOR_RL = anchor(1, 0, 0),
    ANCHOR_RC = anchor(1, 0.5F, 0),
    ANCHOR_RR = anchor(1, 1, 0);

  static Anchor anchorLL() {
    return ANCHOR_LL;
  }

  static Anchor anchorLC() {
    return ANCHOR_LC;
  }

  static Anchor anchorLR() {
    return ANCHOR_LR;
  }

  static Anchor anchorCL() {
    return ANCHOR_CL;
  }

  static Anchor anchorCC() {
    return ANCHOR_CC;
  }

  static Anchor anchorCR() {
    return ANCHOR_CR;
  }

  static Anchor anchorRL() {
    return ANCHOR_RL;
  }

  static Anchor anchorRC() {
    return ANCHOR_RC;
  }

  static Anchor anchorRR() {
    return ANCHOR_RR;
  }
}
