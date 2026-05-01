package necron.ui.layout;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Axis {
  X(1, 0),
  Y(0, 1),
  ;

  public final int x, y;
}
