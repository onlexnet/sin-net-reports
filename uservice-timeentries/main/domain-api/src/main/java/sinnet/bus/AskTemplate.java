package sinnet.bus;

import io.vertx.core.Future;
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

  protected final Future<REPLY> ask(ASK ask) {
    var query = JsonObject.mapFrom(ask);
    var options = new DeliveryOptions().setTracingPolicy(TracingPolicy.ALWAYS);
    return eventBus
        .request(address, query, options)
        .map(it -> {
          var body = it.body();
          var reply = JsonObject.mapFrom(body).mapTo(replyClass);
          return reply;
        });
  }
}
