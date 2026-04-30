package necron.ui.react;

public class UpdaterReact extends SerialReact<Object> {
  @Override
  public Object get() {
    return null;
  }

  public void update() {
    markDirty();
    triggerHooks(null, null);
  }
}
