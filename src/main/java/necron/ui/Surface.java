package necron.ui;

import necron.ui.react.CalcReact;

import static necron.ui.react.React.useCalc;
import static yqloss.E._with;

public class Surface {
  public static final CalcReact<Float> WIDTH = useCalc(() -> _with(
    Lazy.MC.getWindow(), x -> (float) x.getWidth() / x.getGuiScale()
  ));

  public static final CalcReact<Float> HEIGHT = useCalc(() -> _with(
    Lazy.MC.getWindow(), x -> (float) x.getHeight() / x.getGuiScale()
  ));

  public static final CalcReact<Float> SCALE = useCalc(() -> _with(
    Lazy.MC.getWindow(), x -> (float) x.getGuiScale()
  ));

  public static final CalcReact<Float> PIXEL = useCalc(() -> _with(
    Lazy.MC.getWindow(), x -> 1F / x.getGuiScale()
  ));

  public static final CalcReact<Float> LINE_WIDTH = useCalc(
    PIXEL, x -> x * 2
  );

  public static void update() {
    WIDTH.forceUpdate();
    HEIGHT.forceUpdate();
    SCALE.forceUpdate();
    PIXEL.forceUpdate();
  }
}
