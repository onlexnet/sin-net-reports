package sinnet.ports.timeentries;

import java.util.function.Consumer;

/** 
 * Simple wrapper for non-closeable beans, managed by DI.
 * Allows to close beans when ApplicationConext is closing.
 */
public interface Closeable {
  
  static <T> Of<T> of(T item, Consumer<T> onClose)  {
    return new Of<>(item, () -> onClose.accept(item));
  }

  static <T> Of<T> of(T item, AutoCloseable onClose)  {
    return new Of<>(item, onClose);
  }

  /** The wrapper. */
  record Of<T>(T item, AutoCloseable onClose) implements AutoCloseable {

    @Override
    public void close() throws Exception {
      onClose.close();
    }
  }
}
