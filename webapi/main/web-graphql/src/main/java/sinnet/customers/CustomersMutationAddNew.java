package sinnet.customers;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import sinnet.AskTemplate;
import sinnet.SomeEntity;
import sinnet.bus.EntityId;
import sinnet.bus.commands.RegisterNewCustomer;

@Component
public class CustomersMutationAddNew extends AskTemplate<RegisterNewCustomer, EntityId>
                                     implements GraphQLResolver<CustomersMutation> {

    protected CustomersMutationAddNew() {
        super(RegisterNewCustomer.ADDRESS, EntityId.class);
    }

    CompletableFuture<SomeEntity> addNew(CustomersMutation gcontext, CustomerEntry entry) {
        var cmd = RegisterNewCustomer.builder()
            .projectId(gcontext.getProjectId())
            .customerName(entry.getCustomerName())
            .customerCityName(entry.getCustomerCityName())
            .customerAddress(entry.getCustomerAddress())
            .build();
        return super.ask(cmd).thenApply(m -> new SomeEntity(m.getProjectId(), m.getId(), m.getVersion()));
    }
}

