package necron.ui.event;

import lombok.Value;
import lombok.With;

@With
@Value
public class KeyEvent implements Event {
  Type type;
  int key;
  boolean press;

  public enum Type {
    KEYBOARD,
    MOUSE;
  }
}
