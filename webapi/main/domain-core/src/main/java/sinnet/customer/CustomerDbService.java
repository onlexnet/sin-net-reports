package sinnet.customer;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;
import lombok.extern.slf4j.Slf4j;
import sinnet.TopLevelVerticle;
import sinnet.commands.RegisterNewCustomer;
import sinnet.models.CustomerValue;

@Component
@Slf4j
public class CustomerDbService extends AbstractVerticle implements TopLevelVerticle {

    private final PgPool pgClient;

    @Autowired
    public CustomerDbService(PgPool pgClient) {
        this.pgClient = pgClient;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var address = RegisterNewCustomer.ADDRESS;
        vertx.eventBus().consumer(address, message -> {
            var entityId = UUID.randomUUID();
            this.save(entityId, 1, CustomerValue.builder().build())
                .onComplete(it -> {
                    message.reply(message);
                });
        });

        super.start(startPromise);
    }

    public Future<Boolean> save(UUID entityId, int version, CustomerValue entity) {
        var promise = Promise.<Boolean>promise();
        var values = Tuple.of(entityId,
            version,
            entity.getCustomerName().getValue());
        pgClient.preparedQuery("INSERT INTO"
                + " customers (entity_id, entity_version, customer_name)"
                + " values ($1, $2, $3)")
                .execute(values, ar -> {
                    if (ar.succeeded()) promise.complete(Boolean.TRUE);
                    else promise.complete(Boolean.FALSE);
                });
        return promise.future();
    }
}

// }
