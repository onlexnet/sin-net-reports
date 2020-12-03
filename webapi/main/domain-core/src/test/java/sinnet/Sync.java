package sinnet;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.vertx.core.Future;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Sync {

    public static <T> AsyncStep<T> when(Supplier<Future<T>> action) {
        var exitGuard = new CompletableFuture<T>();
        action.get()
            .onSuccess(it -> exitGuard.complete(it))
            .onFailure(ex -> exitGuard.completeExceptionally(ex));
        return new AsyncStep<T>(exitGuard);
    }

    public static final class AsyncStep<T> {
        private final CompletableFuture<T> chainStep;
        AsyncStep(CompletableFuture<T> chainStep) {
            this.chainStep = chainStep;
        }

        public <N> AsyncStep<N> and(Function<T, Future<N>> action) {
            var lastStepValue = chainStep.join();
            var exitGuard = new CompletableFuture<N>();
            action.apply(lastStepValue)
                .onSuccess(it -> exitGuard.complete(it))
                .onFailure(ex -> exitGuard.completeExceptionally(ex));
            return new AsyncStep<>(exitGuard);
        }

        public AsyncStep<T> checkpoint(Consumer<T> assertCheck) {
            var lastStepValue = chainStep.join();
            assertCheck.accept(lastStepValue);
            return new AsyncStep<>(chainStep);
        }

        public T get() {
            return chainStep.join();
        }
    }
}
