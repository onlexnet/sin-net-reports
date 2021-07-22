package sinnet.read;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.vertx.core.CompositeFuture;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
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
public class ActionProjectorProvider implements ActionProjector.Provider, ActionProjector {

  @Autowired
  private PgPool pgClient;

  private static final String VIEW_SELECT = "SELECT a.project_id, a.entity_id, a.entity_version, "
            + "a.serviceman_email, a.distance, a.duration, a.date, a.description, a.serviceman_name, "
            + "a.customer_id, "
            + "c.customer_name, c.customer_city_name, c.customer_address "
            + "FROM actions a LEFT JOIN customers c on a.customer_id = c.entity_id ";

  @Override
  public Future<Array<ListItem>> find(UUID projectId, LocalDate from, LocalDate to) {
    var promise = Promise.<Array<ListItem>>promise();

    var findDataQuery = this.pgClient
            .preparedQuery(VIEW_SELECT + " WHERE a.project_id=$1 AND a.date >= $2 AND a.date <= $3");
    var findServicemanQuery = this.pgClient
            .preparedQuery("SELECT email, custom_name from serviceman t WHERE t.project_entity_id=$1");

    var f1 = findDataQuery.execute(Tuple.of(projectId, from, to));
    var f2 = findServicemanQuery.execute(Tuple.of(projectId));

    CompositeFuture.all(f1, f2)
      .onSuccess(ignored -> {
        var items = Array.ofAll(f1.result()).map(this::mapItem);
        var users = Array.ofAll(f2.result()).map(this::mapServiceman)
            .toMap(it -> it._1, it -> it._2);
        var mappedItems = items.map(item -> {
          var servicemanEmail = item.build().getValue().getWho().getValue();
          var servicemanCustomName = users.get(servicemanEmail).getOrElse((String)null);
          return item.servicemanName(servicemanCustomName).build();
        });
        promise.complete(mappedItems);
      })
      .onFailure(ex -> promise.fail(ex));

    //   .onSuccess(rows -> Array.ofAll(rows)
    // , ar -> {
    //   if (ar.succeeded()) {
    //     var rows = ar.result();
    //     var value = Array.ofAll(rows).map(this::map);
    //     promise.complete(value);
    //   } else {
    //     promise.fail(ar.cause());
    //   }
    // });

    return promise
      .future();
  }

  ListItem.ListItemBuilder mapItem(Row row) {
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
    var customerCity = row.getString("customer_city_name");
    var customerAddress = row.getString("customer_address");
    return ListItem.builder()
        .eid(eid)
        .value(value)
        .customerName(customerName)
        .customerCity(customerCity)
        .customerAddress(customerAddress);
  }

  Tuple2<String, String> mapServiceman(Row row) {
    var email = row.getString(0);
    var customName = row.getString(1);
    return new Tuple2<>(email, customName);
  }
}
