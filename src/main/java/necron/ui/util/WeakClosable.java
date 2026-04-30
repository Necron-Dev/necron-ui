package necron.ui.util;

import java.lang.ref.WeakReference;

import static yqloss.E.$also;
import static yqloss.E._throw;

public class WeakClosable implements AutoCloseable {
  private final WeakReference<AutoCloseable> closeable;

  public WeakClosable(AutoCloseable closeable) {
    this.closeable = new WeakReference<>(closeable);
  }

  @Override
  public void close() {
    $also(
      closeable.get(), x -> {
        try {
          x.close();
        } catch (Exception e) {
          _throw(e);
        }
      }
    );
  }
}
