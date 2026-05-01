package necron.ui.react;

import lombok.AllArgsConstructor;

import java.util.List;

import static yqloss.E._cast;

@AllArgsConstructor
public class ConstListReact<T> implements ListReact<T> {
  private static final AutoCloseable DUMMY_HOOK = () -> {};

  private final List<? extends T> list;

  @Override
  public AutoCloseable hookMove(MoveFn<? super T> hook) {
    return DUMMY_HOOK;
  }

  @Override
  public AutoCloseable hookInsert(InsertFn<? super T> hook) {
    return DUMMY_HOOK;
  }

  @Override
  public AutoCloseable hookRemove(RemoveFn<? super T> hook) {
    return DUMMY_HOOK;
  }

  @Override
  public AutoCloseable hook(HookFn<? super List<T>> hook) {
    return DUMMY_HOOK;
  }

  @Override
  public long getSerial() {
    return 0;
  }

  @Override
  public List<T> get() {
    return _cast(list);
  }
}
