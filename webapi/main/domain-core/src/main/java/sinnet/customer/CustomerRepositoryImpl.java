package sinnet.customer;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.collection.List;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;
import lombok.extern.slf4j.Slf4j;
import sinnet.models.CustomerValue;
import sinnet.models.Entity;
import sinnet.models.EntityId;
import sinnet.models.Name;

@Component
@Slf4j
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

    private final Exception noDataException = new Exception("No data");
    private Future<Entity<CustomerValue>> extractResult(Iterable<Entity<CustomerValue>> candidates) {
        var iter = candidates.iterator();
        if (!iter.hasNext()) return Future.failedFuture(noDataException);
        var item = iter.next();
        return Future.succeededFuture(item);
    }

    @Override
    public Future<Entity<CustomerValue>> get(EntityId id) {
        var whereClause = "entity_id=$1 AND entity_version=$2";
        var values = Tuple.of(id.getId(), id.getVersion());
        return get(whereClause, values)
            .map(this::extractResult)
            .flatMap(it -> it);
    }

    @Override
    public Future<Entity<CustomerValue>> get(UUID id) {
        var whereClause = "entity_id=$1";
        var values = Tuple.of(id);
        return get(whereClause, values)
            .map(this::extractResult)
            .flatMap(it -> it);
    }

    @Override
    public Future<Iterable<Entity<CustomerValue>>> list() {
        return get("1=1", Tuple.tuple())
            .map(it -> it);
    }

    private Future<List<Entity<CustomerValue>>> get(String whereClause, Tuple values) {
        var promise = Promise.<List<Entity<CustomerValue>>>promise();
        pgClient
            .preparedQuery("SELECT"
                         + " entity_id, entity_version, customer_name, customer_city_name, customer_address"
                         + " FROM customers c"
                         + " WHERE " + whereClause)
            .execute(values, ar -> {
                if (ar.succeeded()) {
                    var result = List.<Entity<CustomerValue>>empty();
                    var iter = ar.result();
                    for (var row: iter) {
                        var entityId = row.getUUID("entity_id");
                        var entityVersion = row.getInteger("entity_version");
                        var item = CustomerValue.builder()
                            .customerName(Name.of(row.getString("customer_name")))
                            .customerCityName(Name.of(row.getString("customer_city_name")))
                            .customerAddress(row.getString("customer_address"))
                            .build()
                            .withId(entityId, entityVersion);
                        result = result.append(item);
                    }
                    promise.complete(result);
                } else {
                    log.error("CustomerRepositoryImpl.get", ar.cause());
                    promise.complete(null);
                }
            });
        return promise.future();
    }


}
