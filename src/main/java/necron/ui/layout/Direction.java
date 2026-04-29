package necron.ui.layout;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Direction {
  HORIZONTAL(1, 0),
  VERTICAL(0, 1),
  ;

  public final int x;
  public final int y;
}
