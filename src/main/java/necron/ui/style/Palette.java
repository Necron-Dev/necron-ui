package necron.ui.style;

import lombok.Value;
import lombok.With;
import lombok.val;
import necron.ui.animation.Ease;
import necron.ui.react.BoxReact;
import necron.ui.react.React;

import java.util.Arrays;
import java.util.List;

import static necron.ui.react.React.useBox;
import static necron.ui.react.React.useGradient;

@With
@Value
public class Palette {
  React<Integer>
    background,
    foreground,
    surface,
    surfaceForeground,
    primary,
    primaryForeground,
    secondary,
    secondaryForeground,
    muted,
    mutedForeground,
    success,
    warning,
    danger,
    info,
    border,
    divider;

  public List<React<Integer>> toList() {
    return List.of(
      background,
      foreground,
      surface,
      surfaceForeground,
      primary,
      primaryForeground,
      secondary,
      secondaryForeground,
      muted,
      mutedForeground,
      success,
      warning,
      danger,
      info,
      border,
      divider
    );
  }

  public void update() {
    toList().forEach(React::get);
  }

  public static Palette fromList(List<? extends React<Integer>> colors) {
    var i = 0;
    return new Palette(
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i++),
      colors.get(i)
    );
  }

  public static Palette fromColors(int... colors) {
    return fromList(Arrays.stream(colors).boxed().map(React::useConst).toList());
  }

  private static final List<BoxReact<Integer>> GLOBAL_BOXES =
    DefaultPalettes.DARK
      .toList()
      .stream()
      .map(x -> useBox(x.peek()))
      .toList();

  public static final Palette GLOBAL = fromList(
    GLOBAL_BOXES
      .stream()
      .map(color -> useGradient(color, (a, x) -> a.interrupt().next(Ease.EXPO.out(), x, 0, 300)))
      .toList()
  );

  public static void switchGlobal(Palette palette) {
    val list = palette.toList();
    for (var i = 0; i < GLOBAL_BOXES.size(); i++) {
      GLOBAL_BOXES.get(i).set(list.get(i).peek());
    }
  }
}
