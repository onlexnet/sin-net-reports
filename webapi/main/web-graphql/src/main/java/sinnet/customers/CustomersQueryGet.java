package sinnet.customers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import sinnet.MyEntity;
import sinnet.SomeEntity;
import sinnet.bus.query.FindCustomer;

@Component
class CustomersQueryGet
      implements GraphQLResolver<CustomersQuery> {

    @Autowired
    private EventBus eventBus;

    CompletableFuture<Optional<CustomerEntity>> get(CustomersQuery gcontext, MyEntity entityId) {
        var result = new CompletableFuture<Optional<CustomerEntity>>();
        var query = new FindCustomer.Ask(gcontext.getProjectId(), entityId.getEntityId()).json();
        eventBus
            .request(FindCustomer.Ask.ADDRESS, query)
            .onComplete(it -> {
                if (it.succeeded()) {
                    var reply = JsonObject.mapFrom(it.result().body()).mapTo(FindCustomer.Reply.class);
                    var gqlId = new SomeEntity(gcontext.getProjectId(), reply.getEntityId(), reply.getEntityVersion());
                    var gqlValue = reply.getValue();
                    var gqlResult = new CustomerEntity(gqlId, gqlValue);
                    result.complete(Optional.of(gqlResult));
                } else {
                    result.completeExceptionally(it.cause());
                }
            });
        return result;
    }
}

