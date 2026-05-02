package necron.ui.layout;

import lombok.Value;
import lombok.With;
import necron.ui.react.React;

import static necron.ui.layout.Dim.fp;
import static necron.ui.layout.Dim.px;

public class Box {
  @With
  @Value
  public static class Size {
    Dim width, height;
  }

  @With
  @Value
  public static class SizePadding {
    Dim width, height;
    React<Float> paddingTop, paddingRight, paddingBottom, paddingLeft;

    public SizePadding padWidth() {
      return withWidth(width.op(Float::sum, px(paddingLeft)).op(Float::sum, px(paddingRight)));
    }

    public SizePadding padHeight() {
      return withHeight(height.op(Float::sum, px(paddingTop)).op(Float::sum, px(paddingBottom)));
    }

    public SizePadding pad() {
      return padWidth().padHeight();
    }
  }

  public static Size size(Dim width, Dim height) {
    return new Size(width, height);
  }

  public static SizePadding box(
    Dim width,
    Dim height,
    React<Float> paddingTop,
    React<Float> paddingRight,
    React<Float> paddingBottom,
    React<Float> paddingLeft
  ) {
    return new SizePadding(
      width,
      height,
      paddingTop,
      paddingRight,
      paddingBottom,
      paddingLeft
    );
  }

  public static SizePadding box(
    Dim width,
    Dim height,
    float paddingTop,
    float paddingRight,
    float paddingBottom,
    float paddingLeft
  ) {
    return new SizePadding(
      width,
      height,
      fp(paddingTop),
      fp(paddingRight),
      fp(paddingBottom),
      fp(paddingLeft)
    );
  }

  public static SizePadding box(
    Dim width,
    Dim height
  ) {
    return box(
      width,
      height,
      fp(0),
      fp(0),
      fp(0),
      fp(0)
    );
  }

  public static SizePadding box(
    Dim width,
    Dim height,
    React<Float> padding
  ) {
    return box(
      width,
      height,
      padding,
      padding,
      padding,
      padding
    );
  }

  public static SizePadding box(
    Dim width,
    Dim height,
    float padding
  ) {
    return box(
      width,
      height,
      fp(padding)
    );
  }

  public static SizePadding box(
    Dim width,
    Dim height,
    React<Float> paddingVertical,
    React<Float> paddingHorizontal
  ) {
    return box(
      width,
      height,
      paddingVertical,
      paddingHorizontal,
      paddingVertical,
      paddingHorizontal
    );
  }

  public static SizePadding box(
    Dim width,
    Dim height,
    float paddingVertical,
    float paddingHorizontal
  ) {
    return box(
      width,
      height,
      fp(paddingVertical),
      fp(paddingHorizontal)
    );
  }

  public static SizePadding box(
    Dim width,
    Dim height,
    React<Float> paddingTop,
    React<Float> paddingHorizontal,
    React<Float> paddingBottom
  ) {
    return box(
      width,
      height,
      paddingTop,
      paddingHorizontal,
      paddingBottom,
      paddingHorizontal
    );
  }

  public static SizePadding box(
    Dim width,
    Dim height,
    float paddingTop,
    float paddingHorizontal,
    float paddingBottom
  ) {
    return box(
      width,
      height,
      fp(paddingTop),
      fp(paddingHorizontal),
      fp(paddingBottom)
    );
  }
}
