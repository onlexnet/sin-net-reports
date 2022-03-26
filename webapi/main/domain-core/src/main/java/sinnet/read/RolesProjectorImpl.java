package sinnet.read;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PreparedQuery;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import sinnet.models.Email;
import sinnet.read.RolesProjector.Role;

/** Projections implementation using VertX async db client. */
@Service
public class RolesProjectorImpl implements RolesProjector.Provider {

  @Autowired
  private PgPool pgClient;

  private PreparedQuery<RowSet<Row>> searchQuery;

  @PostConstruct
  public void init() {
    searchQuery = this.pgClient.preparedQuery("SELECT 1 "
                + "FROM serviceman sm "
                + "WHERE sm.project_entity_id=$1 AND sm.email=$2");
  }

  @Override
  public Future<Role> find(Email testedPerson, UUID projectId) {
    var promise = Promise.<Role>promise();
    this.searchQuery
          .execute(Tuple.of(projectId, testedPerson.getValue()), ar -> {
            if (!ar.succeeded()) {
              promise.fail(ar.cause());
              return;
            }
            if (ar.result().size() != 0) {
              promise.complete(Role.USER);
              return;
            }
            promise.complete(Role.NONE);
            return;
        });
    return promise.future();
  }
}
