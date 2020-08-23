package sinnet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import sinnet.appevents.ServicesProjection;

/** Allows to listen {@link ServicesProjection.Changed} events. */
@Service
public class ServicesProjectionChangedConfig {


    @Autowired
    private EventBus eventBus;

    /**
     * Handles information when projections is changing.
     *
     * @param event observed event
     */
    @TransactionalEventListener
    public void handleCustom(ServicesProjection.Changed event) {
        var msg = JsonObject.mapFrom(event);
        eventBus.publish("aaa", msg);
    }
}
