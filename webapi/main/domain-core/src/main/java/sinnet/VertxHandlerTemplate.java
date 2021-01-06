package sinnet;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class VertxHandlerTemplate<T1, T2> implements Handler<Message<JsonObject>> {

    private final Class<T1> requestClass;

    protected VertxHandlerTemplate(Class<T1> requestClass) {
        this.requestClass = requestClass;
    }

    @Override
    public void handle(Message<JsonObject> message) {
        var msg = JsonObject.mapFrom(message.body()).mapTo(requestClass);
        onRequest(msg)
            .onSuccess(it -> {
                var jsonReply = JsonObject.mapFrom(it);
                message.reply(jsonReply);
            })
            .onFailure(ex -> {
                log.error("Error", ex);
                message.reply(null);
            });
    }

    protected abstract Future<T2> onRequest(T1 request);
}
