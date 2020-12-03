package sinnet.customers;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import io.vavr.collection.List;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import sinnet.AskTemplate;
import sinnet.Entity;
import sinnet.SomeEntity;
import sinnet.bus.EntityId;
import sinnet.bus.commands.RegisterNewCustomer;
import sinnet.bus.query.FindCustomer;
import sinnet.bus.query.FindCustomers;

@Value
public class CustomersOperations {
    private UUID projectId;
}

@Component
class CustomersOperationsResolverList extends AskTemplate<FindCustomers.Ask, FindCustomers.Reply>
                                      implements GraphQLResolver<CustomersOperations> {

    CustomersOperationsResolverList() {
        super(FindCustomers.Ask.ADDRESS, FindCustomers.Reply.class);
    }

    CompletableFuture<List<CustomerModel>> list(CustomersOperations gcontext) {
        var ask = new FindCustomers.Ask();
        return super.ask(ask)
            .thenApply(it -> List.of(it.getData())
                .map(o -> CustomerModel.builder()
                                        .customerName(o.getValue().getCustomerCityName().getValue())
                                        .customerCityName(o.getValue().getCustomerCityName().getValue())
                                        .customerAddress(o.getValue().getCustomerAddress())
                                        .build()
                ));
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
class CustomersOperationsResolverAddNew extends AskTemplate<RegisterNewCustomer, EntityId>
                                        implements GraphQLResolver<CustomersOperations> {

    protected CustomersOperationsResolverAddNew() {
        super(RegisterNewCustomer.ADDRESS, EntityId.class);
    }

    // @Autowired
    // private EventBus eventBus;

    CompletableFuture<SomeEntity> addNew(CustomersOperations gcontext, CustomerEntry entry) {
        var cmd = RegisterNewCustomer.builder()
            .projectId(gcontext.getProjectId())
            .customerName(entry.getCustomerName())
            .customerCityName(entry.getCustomerCityName())
            .customerAddress(entry.getCustomerAddress())
            .build();
        return super.ask(cmd).thenApply(m -> new SomeEntity(m.getProjectId(), m.getId(), m.getVersion()));
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
