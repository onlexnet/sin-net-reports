package sinnet;

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
import sinnet.models.Email;

@Service
public class UsersProviderImpl implements UsersProvider {

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
    public Mono<Stream<UserModel>> search(UUID projectId, Email serviceMan) {
        return Mono.create(consumer -> {
            this.searchQuery
                .execute(Tuple.of(projectId), ar -> {
                    if (!ar.succeeded()) {
                        consumer.error(ar.cause());
                        return;
                    }
                    var result = Stream.ofAll(ar.result())
                        .map(it -> UserModel.builder()
                                    .email(Email.of(it.getString("email")))
                                    .build());
                    consumer.success(result);
                });
        });
    }
}
