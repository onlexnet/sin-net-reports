package sinnet.action;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.vavr.collection.Stream;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import reactor.core.publisher.Mono;
import sinnet.ActionService;
import sinnet.ServiceValue;
import sinnet.models.ActionDuration;
import sinnet.models.Distance;
import sinnet.models.Email;
import sinnet.models.Entity;
import sinnet.models.Name;

@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private PgPool pgClient;

    private static final String SOURCE = "SELECT entity_id, serviceman_email, distance, duration, date, customer_name, description, serviceman_name "
                                       + "FROM actions it ";
    @Override
    public Mono<Boolean> save(UUID entityId, ServiceValue entity) {
        var values = Tuple.of(entityId,
            entity.getWho().getValue(),
            entity.getWhom().getValue(),
            entity.getWhat(),
            entity.getHowFar().getValue(),
            entity.getHowLong().getValue(),
            entity.getWho().getValue(), entity.getWhen());
        return Mono.create(consumer -> {
        pgClient.preparedQuery("INSERT INTO "
                + "actions (entity_id, serviceman_email, customer_name, description, distance, duration, serviceman_name, date) "
                + "values ($1, $2, $3, $4, $5, $6, $7, $8)")
                .execute(values, ar -> {
                    if (ar.succeeded()) consumer.success(Boolean.TRUE);
                    else consumer.error(ar.cause());
                });
            });
    }


    @Override
    public Mono<Stream<Entity<ServiceValue>>> find(LocalDate from, LocalDate to) {
        var findQuery = this.pgClient
                .preparedQuery(SOURCE + " WHERE it.date >= $1 AND it.date <= $2");
        return Mono.create(consumer -> {
                    findQuery.execute(Tuple.of(from, to), ar -> {
                    if (ar.succeeded()) {
                        var rows = ar.result();
                        var value = Stream
                            .ofAll(rows)
                            .map(ActionServiceImpl::map);
                        consumer.success(value);
                    } else {
                        consumer.error(ar.cause());
                    }
                });
            });
    }

    @Override
    public Mono<Entity<ServiceValue>> find(UUID entityId) {
        var findQuery = this.pgClient
                .preparedQuery(SOURCE + " WHERE it.entity_id = $1");
        return Mono.create(consumer -> {
                    findQuery.execute(Tuple.of(entityId), ar -> {
                    if (ar.succeeded()) {
                        var rows = ar.result();
                        var maybeValue = Stream
                            .ofAll(rows)
                            .map(ActionServiceImpl::map)
                            .headOption();
                        if (maybeValue.isDefined()) consumer.success(maybeValue.get());
                        else consumer.success();
                    } else {
                        consumer.error(ar.cause());
                    }
                });
            });
    }

    private static Entity<ServiceValue> map(Row row) {
        return ServiceValue.builder()
        .who(Email.of(row.getString("serviceman_email")))
        .howFar(Distance.of(row.getInteger("distance")))
        .howLong(ActionDuration.of(row.getInteger("duration")))
        .whom(Name.of(row.getString("customer_name")))
        .what(row.getString("description"))
        .when(row.getLocalDate("date"))
        .build()
        .withId(row.getUUID("entity_id"));
    }

    @Override
    public Mono<Boolean> update(UUID entityId, ServiceValue entity) {
        var values = Tuple.of(entityId,
            entity.getWho().getValue(),
            entity.getWhom().getValue(),
            entity.getWhat(),
            entity.getHowFar().getValue(),
            entity.getHowLong().getValue(),
            entity.getWho().getValue(), entity.getWhen());
        return Mono.create(consumer -> {
            pgClient.preparedQuery("UPDATE "
                + "actions SET "
                + "serviceman_email=$2, customer_name=$3, description=$4, distance=$5, duration=$6, serviceman_name=$7, date=$8 "
                + "where entity_id=$1")
                .execute(values, ar -> {
                    if (ar.succeeded()) consumer.success(Boolean.TRUE);
                    else consumer.error(ar.cause());
                });
        });
    }
}
