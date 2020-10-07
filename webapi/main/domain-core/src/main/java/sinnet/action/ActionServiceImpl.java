package sinnet.action;

import java.time.LocalDate;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.vavr.collection.Stream;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PreparedQuery;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import reactor.core.publisher.Mono;
import sinnet.ActionService;
import sinnet.Distance;
import sinnet.Entity;
import sinnet.Name;
import sinnet.ServiceEntity;

@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private PgPool pgClient;

    private PreparedQuery<RowSet<Row>> findQuery;

    @PostConstruct
    public void init() {
        findQuery = this.pgClient
                .preparedQuery("SELECT entity_id, distance, date, customer_name, description, serviceman_name "
                + "FROM actions it "
                + "WHERE it.date >= $1 AND it.date <= $2");
    }

    @Override
    public Mono<Void> save(UUID entityId, ServiceEntity entity) {
        var values = Tuple.of(entityId,
            entity.getWhom().getValue(),
            entity.getWhat(), entity.getHowFar().getValue(), entity.getHowLong(),
            entity.getWho().getValue(), entity.getWhen());
        return Mono.create(consumer -> {
        pgClient.preparedQuery("INSERT INTO "
                + "actions (entity_id, customer_name, description, distance, duration, serviceman_name, date) "
                + "values ($1, $2, $3, $4, $5, $6, $7)")
                .execute(values, ar -> {
                    if (ar.succeeded()) consumer.success();
                    else consumer.error(ar.cause());
                });
            });
    }


    @Override
    public Mono<Stream<Entity<ServiceEntity>>> find(LocalDate from, LocalDate to) {
        return Mono.create(consumer -> {
                    findQuery.execute(Tuple.of(from, to), ar -> {
                    if (ar.succeeded()) {
                        var rows = ar.result();
                        var value = Stream
                            .ofAll(rows)
                            .map(it -> ServiceEntity.builder()
                                .howFar(Distance.of(it.getInteger("distance")))
                                .whom(Name.of(it.getString("customer_name")))
                                .what(it.getString("description"))
                                .when(it.getLocalDate("date"))
                                .build()
                                .withId(it.getUUID("entity_id")));
                        consumer.success(value);
                    } else {
                        consumer.error(ar.cause());
                    }
                });
            });
    }
}
