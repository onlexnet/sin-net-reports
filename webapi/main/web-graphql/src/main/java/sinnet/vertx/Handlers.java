package sinnet.vertx;

import java.util.function.Consumer;

import org.slf4j.Logger;

import io.grpc.stub.StreamObserver;
import io.vavr.Function1;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class Handlers {

    public static <T, U> Handler<AsyncResult<T>> logged(Logger logger, StreamObserver<U> responseObserver, Function1<T, U> handler) {
        return new LoggedHandlerResponse<>(logger, responseObserver, handler);
    }

    public static <T, U> Handler<AsyncResult<T>> logged(Logger logger, Function1<T, U> handler) {
        return new LoggedHandler<>(logger, handler);
    }

    private static final class LoggedHandlerResponse<T, U> extends LoggedHandlerBase<T, U>  {
        LoggedHandlerResponse(Logger logger, StreamObserver<U> responseObserver, Function1<T, U> successHandler) {
            super(logger, successHandler, it -> {
                responseObserver.onNext(it);
                responseObserver.onCompleted();
            });
        }
    }


    private static final class LoggedHandler<T, U> extends LoggedHandlerBase<T, U> {
        LoggedHandler(Logger logger, Function1<T, U> successHandler) {
            super(logger, successHandler, it -> { });
        }
    }

    @RequiredArgsConstructor
    private static class LoggedHandlerBase<T, U> implements Handler<AsyncResult<T>> {
        private final Logger logger;
        private final Function1<T, U> successHandler;
        private final Consumer<U> postSuccessHandler;
        @Override
        public void handle(AsyncResult<T> event) {
            if (event.failed()) {
                var ex = event.cause();
                logger.error("Error", ex);
            } else {
                var result = event.result();
                var updated = successHandler.apply(result);
                postSuccessHandler.accept(updated);
            }
        }
    }
}
