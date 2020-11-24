package sinnet.customer;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;
import sinnet.models.CustomerValue;

public class CustomerRepositoryImpl implements CustomerRepository {

    private final PgPool pgClient;

    @Autowired
    public CustomerRepositoryImpl(PgPool pgclient) {
        this.pgClient = pgclient;
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
