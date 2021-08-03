package sinnet.bus;

import java.util.concurrent.CompletableFuture;

import org.springframework.remoting.RemoteAccessException;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.tracing.TracingPolicy;

public abstract class AskTemplate<ASK, REPLY> {

  private final EventBus eventBus;

  private final String address;
  private final Class<REPLY> replyClass;

  protected AskTemplate(String address, Class<REPLY> replyClass, EventBus eventBus) {
    this.address = address;
    this.replyClass = replyClass;
    this.eventBus = eventBus;
  }

  protected final CompletableFuture<REPLY> ask(ASK ask) {
    var result = new CompletableFuture<REPLY>();
    var query = JsonObject.mapFrom(ask);
    var options = new DeliveryOptions().setTracingPolicy(TracingPolicy.ALWAYS);
    eventBus
        .request(address, query, options)
        .onFailure(it -> result.completeExceptionally(new RemoteAccessException(it.getMessage())))
        .onSuccess(it -> {
          var body = it.body();
          var reply = JsonObject.mapFrom(body).mapTo(replyClass);
          result.completeAsync(() -> reply);
        });
    return result;
  }
}
