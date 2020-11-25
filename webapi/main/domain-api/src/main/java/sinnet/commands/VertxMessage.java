package sinnet.commands;

import io.vertx.core.json.JsonObject;

public interface VertxMessage {
    default JsonObject json() {
        return JsonObject.mapFrom(this);
    }
}
