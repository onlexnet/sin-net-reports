package sinnet;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import sinnet.bus.JsonMessage;

public abstract class AskTemplate<ASK extends JsonMessage, REPLY extends JsonMessage> {

    @Autowired
    private EventBus eventBus;

    private final String address;
    private final Class<REPLY> replyClass;

    protected AskTemplate(String address, Class<REPLY> replyClass) {
        this.address = address;
        this.replyClass = replyClass;
    }

    protected final CompletableFuture<Optional<REPLY>> ask(ASK ask) {
        var result = new CompletableFuture<Optional<REPLY>>();
        var query = ask.json();
        eventBus
            .request(address, query)
            .onComplete(it -> {
                if (it.succeeded()) {
                    var reply = JsonObject.mapFrom(it.result().body()).mapTo(replyClass);
                    result.completeAsync(() -> Optional.of(reply));
                } else {
                    result.completeExceptionally(it.cause());
                }
            });
        return result;
    }
}
