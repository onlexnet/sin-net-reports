package sinnet.customers;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import sinnet.bus.query.FindCustomer;

@Component
class CustomersQueryGet
      implements GraphQLResolver<CustomersQuery> {

    @Autowired
    private EventBus eventBus;

    CompletableFuture<Optional<CustomerModel>> get(CustomersQuery gcontext, UUID entityId) {
        var result = new CompletableFuture<Optional<CustomerModel>>();
        var query = new FindCustomer.Ask(gcontext.getProjectId(), entityId).json();
        eventBus
            .request(FindCustomer.Ask.ADDRESS, query)
            .onComplete(it -> {
                if (it.succeeded()) {
                    var reply = JsonObject.mapFrom(it.result().body()).mapTo(FindCustomer.Reply.class);
                    var gqlModel = CustomerModel.builder()
                        .customerName(reply.getValue().getCustomerName().getValue())
                        .customerCityName(reply.getValue().getCustomerCityName().getValue())
                        .customerAddress(reply.getValue().getCustomerAddress())
                        .build();
                    result.complete(Optional.of(gqlModel));
                } else {
                    result.completeExceptionally(it.cause());
                }
            });
        return result;
    }
}

