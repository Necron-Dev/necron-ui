package necron.ui.callback;

import lombok.val;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface FrameCallback {
  Event<FrameCallback> EVENT = EventFactory.createArrayBacked(
    FrameCallback.class,
    listeners -> () -> {
      for (val listener : listeners) {
        listener.onFrame();
      }
    }
  );

  void onFrame();
}
