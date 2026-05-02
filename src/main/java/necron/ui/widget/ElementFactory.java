package necron.ui.widget;

import lombok.Value;
import necron.ui.react.WithKey;

import java.util.function.Supplier;

@Value
public class ElementFactory implements WithKey {
  Key key;
  Supplier<Element> constructor;
}
