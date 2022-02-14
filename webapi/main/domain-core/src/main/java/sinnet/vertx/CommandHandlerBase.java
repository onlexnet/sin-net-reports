package sinnet.vertx;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/** Hides repeatable code of handling request as Json type and convert response back to Json. */
@Slf4j
public abstract class CommandHandlerBase<T1, T2> implements Handler<Message<JsonObject>> {

  private final Class<T1> requestClass;

  protected CommandHandlerBase(Class<T1> requestClass) {
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
          message.fail(0, ex.getMessage());
        });
  }

  protected abstract Future<T2> onRequest(T1 request);
}
