package necron.ui.react;

import java.util.List;

public interface ListReact<T> extends React<List<T>> {
  AutoCloseable hookMove(MoveFn<? super T> hook);

  AutoCloseable hookInsert(InsertFn<? super T> hook);

  AutoCloseable hookRemove(RemoveFn<? super T> hook);

  default ListReact<T> hookingMove(MoveFn<? super T> hook) {
    hookMove(hook);
    return this;
  }

  default ListReact<T> hookingInsert(InsertFn<? super T> hook) {
    hookInsert(hook);
    return this;
  }

  default ListReact<T> hookingRemove(RemoveFn<? super T> hook) {
    hookRemove(hook);
    return this;
  }
}
