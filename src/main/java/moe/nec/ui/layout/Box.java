package moe.nec.ui.layout;

import lombok.With;
import lombok.experimental.UtilityClass;
import moe.nec.ui.react.React;

@UtilityClass
public class Box {
  @With
  public record Size(
    Dim width,
    Dim height
  ) {}

  @With
  public record SizePadding(
    Dim width,
    Dim height,
    React<Float> paddingTop,
    React<Float> paddingRight,
    React<Float> paddingBottom,
    React<Float> paddingLeft
  ) {
    public Size asSize() {
      return new Size(width, height);
    }

    public SizePadding padWidth() {
      return withWidth(width.plus(paddingLeft).plus(paddingRight));
    }

    public SizePadding padHeight() {
      return withHeight(height.plus(paddingTop).plus(paddingBottom));
    }

    public SizePadding pad() {
      return padWidth().padHeight();
    }
  }

  public Size size(Dim width, Dim height) {
    return new Size(width, height);
  }

  public SizePadding box(
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

  public SizePadding box(
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
      Dim.pxHook(paddingTop),
      Dim.pxHook(paddingRight),
      Dim.pxHook(paddingBottom),
      Dim.pxHook(paddingLeft)
    );
  }

  public SizePadding box(
    Dim width,
    Dim height
  ) {
    return box(
      width,
      height,
      Dim.zeroHook(),
      Dim.zeroHook(),
      Dim.zeroHook(),
      Dim.zeroHook()
    );
  }

  public SizePadding box(
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

  public SizePadding box(
    Dim width,
    Dim height,
    float padding
  ) {
    return box(
      width,
      height,
      Dim.pxHook(padding)
    );
  }

  public SizePadding box(
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

  public SizePadding box(
    Dim width,
    Dim height,
    float paddingVertical,
    float paddingHorizontal
  ) {
    return box(
      width,
      height,
      Dim.pxHook(paddingVertical),
      Dim.pxHook(paddingHorizontal)
    );
  }

  public SizePadding box(
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

  public SizePadding box(
    Dim width,
    Dim height,
    float paddingTop,
    float paddingHorizontal,
    float paddingBottom
  ) {
    return box(
      width,
      height,
      Dim.pxHook(paddingTop),
      Dim.pxHook(paddingHorizontal),
      Dim.pxHook(paddingBottom)
    );
  }
}
