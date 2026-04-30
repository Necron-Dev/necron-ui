package necron.ui.react;

@FunctionalInterface
public interface MoveFn<T> {
  void onMove(int from, int to, T value);
}
