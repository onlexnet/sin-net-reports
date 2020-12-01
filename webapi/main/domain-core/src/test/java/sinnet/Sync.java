package sinnet;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import io.vertx.core.Future;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Sync {

    public static <T> SyncStep<T> wait(Supplier<Future<T>> action) {
        var exitGuard = new CompletableFuture<T>();
        action.get()
            .onSuccess(it -> exitGuard.complete(it))
            .onFailure(ex -> exitGuard.completeExceptionally(ex));
        return new SyncStep<T>(exitGuard);
    }

    public static final class SyncStep<T> {
        private final CompletableFuture<T> chainStep;
        SyncStep(CompletableFuture<T> chainStep) {
            this.chainStep = chainStep;
        }

        public <N> SyncStep<N> and(Function<T, Future<N>> action) {
            var lastStepValue = chainStep.join();
            var exitGuard = new CompletableFuture<N>();
            action.apply(lastStepValue)
                .onSuccess(it -> exitGuard.complete(it))
                .onFailure(ex -> exitGuard.completeExceptionally(ex));
                return new SyncStep<>(exitGuard);
        }

        public T get() {
            return chainStep.join();
        }
    }
}
