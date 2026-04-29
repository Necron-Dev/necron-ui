package necron.ui.util;

import lombok.SneakyThrows;
import lombok.val;

import java.util.ArrayDeque;
import java.util.Queue;

public interface SmartClosable extends AutoCloseable {
  @SneakyThrows
  @Override
  default void close() {
    doClose();
    val queue = new ArrayDeque<>();
    collectObjectsToClose(queue);
    while (!queue.isEmpty()) {
      val object = queue.poll();
      switch (object) {
        case SmartClosable sc -> {
          sc.doClose();
          sc.collectObjectsToClose(queue);
        }
        case AutoCloseable ac -> ac.close();
        default -> {}
      }
    }
  }

  default void doClose() {}

  default void collectObjectsToClose(Queue<? super Object> queue) {}
}
