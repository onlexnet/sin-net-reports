package sinnet;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.vavr.collection.Stream;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PreparedQuery;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import reactor.core.publisher.Mono;
import sinnet.models.Email;
import sinnet.read.UserModel;
import sinnet.read.UsersProjector;

/** Projections implementation using VertX async db client. */
@Service
public class UsersProjectorImpl implements UsersProjector {

  @Autowired
  private PgPool pgClient;

  private PreparedQuery<RowSet<Row>> searchQuery;

  @PostConstruct
  public void init() {
    searchQuery = this.pgClient.preparedQuery("SELECT "
                + "sm.project_entity_id, sm.entity_id, sm.email "
                + "FROM serviceman sm "
                + "WHERE sm.project_entity_id=$1");
  }

  @Override
  public Future<Stream<UserModel>> search(UUID projectId, Email serviceMan) {
    var promise = Promise.<Stream<UserModel>>promise();
    this.searchQuery
          .execute(Tuple.of(projectId), ar -> {
            if (!ar.succeeded()) {
              promise.fail(ar.cause());
              return;
            }
            var result = Stream.ofAll(ar.result())
                .map(it -> UserModel.builder()
                            .email(Email.of(it.getString("email")))
                            .build());
            promise.complete(result);
          });
    return promise.future();
  }
}
