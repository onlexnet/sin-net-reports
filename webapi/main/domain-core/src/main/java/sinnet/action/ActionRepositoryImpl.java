package sinnet.action;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.vavr.collection.Array;
import io.vavr.collection.Stream;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;
import sinnet.ActionRepository;
import sinnet.models.ActionValue;
import sinnet.models.Entity;
import sinnet.models.EntityId;

@Service
public class ActionRepositoryImpl implements ActionRepository {

    @Autowired
    private PgPool pgClient;

    private static final String SOURCE = "SELECT entity_id, entity_version, "
                                       + "serviceman_email, distance, duration, date, customer_name, description, serviceman_name "
                                       + "FROM actions it ";
    @Override
    public Future<Boolean> save(EntityId entityId, ActionValue entity) {
        var promise = Promise.<Boolean>promise();
        var values = Tuple.of(entityId.getProjectId(),
            entityId.getId(),
            entityId.getVersion(),
            entity.getWho().getValue(),
            entity.getWhom().getValue(),
            entity.getWhat(),
            entity.getHowFar().getValue(),
            entity.getHowLong().getValue(),
            entity.getWho().getValue(),
            entity.getWhen());
        pgClient.preparedQuery("INSERT INTO "
                + "actions (project_id, entity_id, entity_version, serviceman_email, customer_name, description, distance, duration, serviceman_name, date) "
                + "values ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)")
                .execute(values, ar -> {
                    if (ar.succeeded()) promise.complete(Boolean.TRUE);
                    else promise.fail(ar.cause());
                });
        return promise.future();
    }


    @Override
    public Future<Array<Entity<ActionValue>>> find(LocalDate from, LocalDate to) {
        var promise = Promise.<Array<Entity<ActionValue>>>promise();
        var findQuery = this.pgClient
                .preparedQuery(SOURCE + " WHERE it.date >= $1 AND it.date <= $2");
        findQuery.execute(Tuple.of(from, to), ar -> {
            if (ar.succeeded()) {
                var rows = ar.result();
                var value = Array
                    .ofAll(rows)
                    .map(Mapper::map);
                promise.complete(value);
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    @Override
    public Future<Entity<ActionValue>> find(UUID projectId, UUID entityId) {
        var promise = Promise.<Entity<ActionValue>>promise();
        var findQuery = this.pgClient
            .preparedQuery(SOURCE + " WHERE it.project_id=$1 AND it.entity_id=$1");
                findQuery.execute(Tuple.of(projectId, entityId), ar -> {
                    if (ar.succeeded()) {
                        var rows = ar.result();
                        var maybeValue = Stream
                            .ofAll(rows)
                            .map(Mapper::map)
                            .headOption();
                        if (maybeValue.isDefined()) promise.complete(maybeValue.get());
                        else promise.complete();
                    } else {
                        promise.fail(ar.cause());
                    }
            });
        return promise.future();
    }

    @Override
    public Future<Boolean> update(EntityId id, ActionValue entity) {
        var promise = Promise.<Boolean>promise();
        var values = Tuple.of(id.getProjectId(),
            id.getId(),
            id.getVersion(),
            entity.getWho().getValue(),
            entity.getWhom().getValue(),
            entity.getWhat(),
            entity.getHowFar().getValue(),
            entity.getHowLong().getValue(),
            entity.getWho().getValue(), entity.getWhen());
        pgClient.preparedQuery("UPDATE "
                + "actions SET "
                + "entity_version=$2+1, serviceman_email=$3, customer_name=$4, description=$5, distance=$6, duration=$7, serviceman_name=$8, date=$9 "
                + "where entity_id=$1 AND entity_version=$2")
                .execute(values, ar -> {
                    if (ar.succeeded()) promise.complete(Boolean.TRUE);
                    else promise.fail(ar.cause());
                });
        return promise.future();
    }
}
