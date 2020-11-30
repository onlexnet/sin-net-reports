package sinnet.customers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import sinnet.AskTemplate;
import sinnet.Entity;
import sinnet.SomeEntity;
import sinnet.bus.commands.RegisterNewCustomer;
import sinnet.bus.EntityId;
import sinnet.bus.query.FindCustomer;
import sinnet.bus.query.FindCustomers;

public class CustomersOperations {
}

@Component
class CustomersOperationsResolverList extends AskTemplate<FindCustomers.Ask, FindCustomers.Reply>
                                      implements GraphQLResolver<CustomersOperations> {

    CustomersOperationsResolverList() {
        super(FindCustomers.Ask.ADDRESS, FindCustomers.Reply.class);
    }

    CompletableFuture<List<CustomerModel>> list(CustomersOperations gcontext) {
        var ask = new FindCustomers.Ask();
        var r =  super.ask(ask).thenApply(it -> {
            return Collections.<CustomerModel>emptyList();
        });
        return r;
    }
}

@Component
class CustomersOperationsResolverGet
      implements GraphQLResolver<CustomersOperations> {

    @Autowired
    private EventBus eventBus;

    CompletableFuture<Optional<CustomerModel>> get(CustomersOperations gcontext, UUID entityId) {
        var result = new CompletableFuture<Optional<CustomerModel>>();
        var query = new FindCustomer.Ask(entityId).json();
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

@Component
class CustomersOperationsResolverAddNew
      implements GraphQLResolver<CustomersOperations> {

    @Autowired
    private EventBus eventBus;

    CompletableFuture<SomeEntity> addNew(CustomersOperations gcontext, CustomerEntry entry) {
        var result = new CompletableFuture<SomeEntity>();
        var cmd = RegisterNewCustomer.builder()
            .customerName(entry.getCustomerName())
            .customerCityName(entry.getCustomerCityName())
            .customerAddress(entry.getCustomerAddress())
            .build();
        eventBus
            .request(RegisterNewCustomer.ADDRESS, cmd.json())
            .onComplete(it -> {
                if (it.succeeded()) {
                    Optional.of(it.result().body())
                        .map(JsonObject::mapFrom)
                        .map(m -> m.mapTo(EntityId.class))
                        .map(m -> new SomeEntity(m.getId(), m.getVersion()))
                        .ifPresent(result::complete);
                } else {
                    result.completeExceptionally(it.cause());
                }
            });
        return result;
    }
}

@Data
class CustomerEntry {
    private String customerName;
    private String customerCityName;
    private String customerAddress;
}

@Value
@Builder
class CustomerModel {
    private String customerName;
    private String customerCityName;
    private String customerAddress;
}

@Value
class CustomerEntity {
    private Entity id;
}

@Component
class CustomerModelResolverPayload
      implements GraphQLResolver<CustomerEntity> {
    CustomerEntry getData(CustomerEntity gcontext) {
        return null;
    }
}
