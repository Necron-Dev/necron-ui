package necron.ui.layout;

import lombok.Value;
import lombok.With;
import necron.ui.react.React;

import static necron.ui.layout.Dim.fp;

@With
@Value
public class Padding {
  React<Float> top, right, bottom, left;

  public static Padding padding(
    React<Float> paddingTop,
    React<Float> paddingRight,
    React<Float> paddingBottom,
    React<Float> paddingLeft
  ) {
    return new Padding(
      paddingTop,
      paddingRight,
      paddingBottom,
      paddingLeft
    );
  }

  public static Padding padding(
    float paddingTop,
    float paddingRight,
    float paddingBottom,
    float paddingLeft
  ) {
    return new Padding(
      fp(paddingTop),
      fp(paddingRight),
      fp(paddingBottom),
      fp(paddingLeft)
    );
  }

  public static Padding padding() {
    return padding(
      fp(0),
      fp(0),
      fp(0),
      fp(0)
    );
  }

  public static Padding padding(
    React<Float> padding
  ) {
    return padding(
      padding,
      padding,
      padding,
      padding
    );
  }

  public static Padding padding(
    float padding
  ) {
    return padding(
      fp(padding)
    );
  }

  public static Padding padding(
    React<Float> paddingVertical,
    React<Float> paddingHorizontal
  ) {
    return padding(
      paddingVertical,
      paddingHorizontal,
      paddingVertical,
      paddingHorizontal
    );
  }

  public static Padding padding(
    float paddingVertical,
    float paddingHorizontal
  ) {
    return padding(
      fp(paddingVertical),
      fp(paddingHorizontal)
    );
  }

  public static Padding padding(
    React<Float> paddingTop,
    React<Float> paddingHorizontal,
    React<Float> paddingBottom
  ) {
    return padding(
      paddingTop,
      paddingHorizontal,
      paddingBottom,
      paddingHorizontal
    );
  }

  public static Padding padding(
    float paddingTop,
    float paddingHorizontal,
    float paddingBottom
  ) {
    return padding(
      fp(paddingTop),
      fp(paddingHorizontal),
      fp(paddingBottom)
    );
  }
}
