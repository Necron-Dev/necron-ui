package necron.ui;

import necron.ui.react.CalcReact;

import static necron.ui.react.React.useCalc;

public class Input {
  public static final CalcReact<Float> MOUSE_X = useCalc(
    () -> (float) Lazy.MC.mouseHandler.getScaledXPos(Lazy.MC.getWindow())
  );

  public static final CalcReact<Float> MOUSE_Y = useCalc(
    () -> (float) Lazy.MC.mouseHandler.getScaledYPos(Lazy.MC.getWindow())
  );

  public static void update() {

  }
}
