package sinnet.project;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.collection.Array;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;
import sinnet.read.Project;

@Component
public class ProjectRepositoryImpl implements Project.Repository, Project {

    private final PgPool pgClient;

    @Autowired
    public ProjectRepositoryImpl(PgPool pgclient) {
        this.pgClient = pgclient;
    }

    public Future<Boolean> save(UUID projectId) {
        var promise = Promise.<Boolean>promise();
        var values = Tuple.of(projectId);
        pgClient
            .preparedQuery("INSERT INTO"
                         + " projects (entity_id)"
                         + " values ($1)")
            .execute(values, ar -> {
                    if (ar.succeeded()) promise.complete(Boolean.TRUE);
                    else promise.fail(ar.cause());
                });
        return promise.future();
    }

    @Override
    public Future<Array<Entity>> get(String filterByEmail) {
        var promise = Promise.<Array<Entity>>promise();
        var values = Tuple.of(filterByEmail);
        pgClient
            .preparedQuery("SELECT p.entity_id, p.name "
                         + "FROM projects p INNER JOIN serviceman s ON p.entity_id = s.project_entity_id "
                         + "WHERE s.email=$1")
            .execute(values, ar -> {
                if (ar.succeeded()) {
                    var rows = ar.result();
                    var projectsIDs = Array
                        .ofAll(rows)
                        .map(it -> new Entity(it.getUUID(0), it.getString(1)));
                    promise.complete(projectsIDs);
                } else {
                    promise.fail(ar.cause());
                }
            });
        return promise.future();
    }
}
