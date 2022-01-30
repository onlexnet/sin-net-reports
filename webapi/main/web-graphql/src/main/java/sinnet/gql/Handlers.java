package sinnet.gql;

import io.vertx.core.Handler;

import java.util.function.Consumer;

import org.slf4j.Logger;

import io.vertx.core.AsyncResult;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class Handlers {
    
    public static <T> Handler<AsyncResult<T>> logged(Logger logger, Consumer<T> handler) {
        return new LoggedHandler<>(logger, handler);
    }

    @RequiredArgsConstructor
    private static class LoggedHandler<T> implements Handler<AsyncResult<T>> {
        private final Logger logger;
        private final Consumer<T> successHandler;

        @Override
        public void handle(AsyncResult<T> event) {
            if (event.failed()) {
                var ex = event.cause();
                logger.error("Error", ex);
            } else {
                var result = event.result();
                successHandler.accept(result);
            }
        }

    }
}
