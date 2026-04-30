package necron.ui.react;

@FunctionalInterface
public interface RemoveFn<T> {
  void onRemove(int index, T value);
}
