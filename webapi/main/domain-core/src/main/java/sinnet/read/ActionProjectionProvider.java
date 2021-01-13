package sinnet.read;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.collection.Array;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import sinnet.models.ActionDuration;
import sinnet.models.ActionValue;
import sinnet.models.Distance;
import sinnet.models.Email;
import sinnet.models.EntityId;

@Component
public class ActionProjectionProvider implements ActionProjection.Provider, ActionProjection {

    @Autowired
    private PgPool pgClient;

    private static final String VIEW_SELECT = "SELECT a.project_id, a.entity_id, a.entity_version, "
            + "a.serviceman_email, a.distance, a.duration, a.date, a.description, a.serviceman_name, "
            + "a.customer_id, "
            + "c.customer_name, c.customer_city, c.customer_address "
            + "FROM actions a LEFT JOIN customers c on a.customer_id = c.entity_id ";

    @Override
    public Future<Array<ListItem>> find(UUID projectId, LocalDate from, LocalDate to) {
        var promise = Promise.<Array<ListItem>>promise();
        var findQuery = this.pgClient
                .preparedQuery(VIEW_SELECT + " WHERE a.project_id=$1 AND a.date >= $2 AND a.date <= $3");
        findQuery.execute(Tuple.of(projectId, from, to), ar -> {
            if (ar.succeeded()) {
                var rows = ar.result();
                var value = Array.ofAll(rows).map(this::map);
                promise.complete(value);
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    ListItem map(Row row) {
        var projectId = row.getUUID("project_id");
        var entityId = row.getUUID("entity_id");
        var entityVersion = row.getInteger("entity_version");
        var eid = EntityId.of(projectId, entityId, entityVersion);
        var value = ActionValue.builder()
            .who(Email.of(row.getString("serviceman_email")))
            .howFar(Distance.of(row.getInteger("distance")))
            .howLong(ActionDuration.of(row.getInteger("duration")))
            .whom(row.getUUID("customer_id"))
            .what(row.getString("description"))
            .when(row.getLocalDate("date"))
            .build();
        var customerName = row.getString("customer_name");
        var customerCity = row.getString("customer_city");
        var customerAddress = row.getString("customer_address");
        return ListItem.builder()
            .eid(eid)
            .value(value)
            .customerName(customerName)
            .customerCity(customerCity)
            .customerAddress(customerAddress)
            .build();
    }
}
