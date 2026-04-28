package moe.nec.ui.react;

import moe.nec.ui.util.SmartClosable;

import java.lang.ref.WeakReference;

import static yqloss.E.$also;

public class Hook implements SmartClosable {
  private final WeakReference<SerialReact<?>> react;
  private final long hookId;

  public Hook(SerialReact<?> react, long hookId) {
    this.react = new WeakReference<>(react);
    this.hookId = hookId;
  }

  @Override
  public void doClose() {
    $also(react.get(), x -> x.unhook(hookId));
  }
}
