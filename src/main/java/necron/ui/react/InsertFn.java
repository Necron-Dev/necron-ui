package necron.ui.react;

@FunctionalInterface
public interface InsertFn<T> {
  void onInsert(int index, T value);
}
