package sinnet;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.google.common.util.concurrent.ListenableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import io.vavr.Function1;

@Component
public class FutureExecutor {

  @Autowired
  private TaskExecutor taskExecutor;

  public <T, U> CompletableFuture<U> asFuture(ListenableFuture<T> future, Function1<T, U> mapper) {
    var result = new CompletableFuture<U>();
    future.addListener(() -> {
      try {
        var actual = future.get();
        var mapped = mapper.apply(actual);
        result.complete(mapped);
      } catch (InterruptedException ex) {
        result.completeExceptionally(ex);
      } catch (ExecutionException ex) {
        result.completeExceptionally(ex);
      }
    }, taskExecutor);
  }
}
