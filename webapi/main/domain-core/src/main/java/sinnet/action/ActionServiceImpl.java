package sinnet.action;

import java.time.LocalDate;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
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
    private DatabaseClient client;

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

        var entry = new ActionsDbModel();
        entry.setEntityId(entityId);
        entry.setDescription(entity.getWhat());
        entry.setServicemanName(entity.getWho().getValue());
        entry.setCustomerName(entity.getWhom().getValue());
        entry.setDate(entity.getWhen());
        entry.setDistance(entity.getHowFar().getValue());
        entry.setDuration(entity.getHowLong());

        return client
            .insert().into(ActionsDbModel.class).using(entry)
            .then();
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
