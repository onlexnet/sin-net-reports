package sinnet.commands;

import io.vertx.core.json.JsonObject;

public interface VertxCommand {
    default JsonObject json() {
        return JsonObject.mapFrom(this);
    }
}
