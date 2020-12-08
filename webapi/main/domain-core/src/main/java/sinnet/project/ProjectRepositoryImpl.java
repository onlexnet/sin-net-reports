package sinnet.project;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;

@Component
public class ProjectRepositoryImpl implements ProjectRepository {

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
}
