package sinnet.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import lombok.extern.slf4j.Slf4j;
import sinnet.TopLevelVerticle;
import sinnet.commands.RegisterNewCustomer;

@Component
@Slf4j
public class CustomerDbService extends AbstractVerticle
                               implements TopLevelVerticle {

    private final PgPool pgClient;

    @Autowired
    public CustomerDbService(PgPool pgClient) {
        this.pgClient = pgClient;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var address = RegisterNewCustomer.ADDRESS;
        vertx.eventBus().consumer(address, message -> {
            message.reply(message);
        });

        super.start(startPromise);
    }
}

//     public Mono<Boolean> save(UUID entityId, CustomerValue entity) {
//         var values = Tuple.of(entityId,
//             entity.getWho().getValue(),
//             entity.getWhom().getValue(),
//             entity.getWhat(),
//             entity.getHowFar().getValue(),
//             entity.getHowLong().getValue(),
//             entity.getWho().getValue(), entity.getWhen());
//         return Mono.create(consumer -> {
//         pgClient.preparedQuery("INSERT INTO "
//                 + "actions (entity_id, entity_version, serviceman_email, customer_name, description, distance, duration, serviceman_name, date) "
//                 + "values ($1, 1, $2, $3, $4, $5, $6, $7, $8)")
//                 .execute(values, ar -> {
//                     if (ar.succeeded()) consumer.success(Boolean.TRUE);
//                     else consumer.error(ar.cause());
//                 });
//             });
//     }
// }
