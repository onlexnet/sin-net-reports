package sinnet.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.control.Option;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;
import sinnet.models.CustomerValue;
import sinnet.models.EntityId;
import sinnet.models.Name;

@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final PgPool pgClient;

    @Autowired
    public CustomerRepositoryImpl(PgPool pgclient) {
        this.pgClient = pgclient;
    }

    public Future<Boolean> save(EntityId id, CustomerValue entity) {
        var promise = Promise.<Boolean>promise();
        var values = Tuple.of(id.getId(), id.getVersion(), entity.getCustomerName().getValue(),
                entity.getCustomerCityName().getValue(), entity.getCustomerAddress());
        pgClient
            .preparedQuery("INSERT INTO"
                         + " customers (entity_id, entity_version, customer_name, customer_city_name, customer_address)"
                         + " values ($1, $2, $3, $4, $5)")
            .execute(values, ar -> {
                    if (ar.succeeded()) promise.complete(Boolean.TRUE);
                    else promise.complete(Boolean.FALSE);
                });
        return promise.future();
    }

    @Override
    public Future<Option<CustomerValue>> get(EntityId id) {
        var promise = Promise.<Option<CustomerValue>>promise();
        var values = Tuple.of(id.getId(), id.getVersion());
        pgClient
            .preparedQuery("SELECT"
                         + " entity_id, entity_version, customer_name, customer_city_name, customer_address"
                         + " FROM customers c"
                         + " WHERE entity_id=$1 AND entity_version=$2")
            .execute(values, ar -> {
                if (ar.succeeded()) {
                    var row = ar.result().iterator().next();
                    var item = CustomerValue.builder()
                        .customerName(Name.of(row.getString("customer_name")))
                        .customerCityName(Name.of(row.getString("customer_city_name")))
                        .customerAddress(row.getString("customer_address"))
                        .build();
                    promise.complete(Option.some(item));
                } else {
                    promise.complete(Option.none());
                }
            });
        return promise.future();
    }
}
