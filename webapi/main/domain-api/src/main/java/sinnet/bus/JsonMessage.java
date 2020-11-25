package sinnet.bus;

import io.vertx.core.json.JsonObject;

public interface JsonMessage {
    default JsonObject json() {
        return JsonObject.mapFrom(this);
    }
}
