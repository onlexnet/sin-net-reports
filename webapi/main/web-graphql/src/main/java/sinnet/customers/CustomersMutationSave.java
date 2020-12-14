package sinnet.customers;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import graphql.GraphQLException;
import graphql.kickstart.tools.GraphQLResolver;
import sinnet.AskTemplate;
import sinnet.SomeEntity;
import sinnet.bus.EntityId;
import sinnet.bus.commands.UpdateCustomerInfo;

@Component
public class CustomersMutationSave extends AskTemplate<UpdateCustomerInfo, EntityId>
                                   implements GraphQLResolver<CustomersMutation> {

    protected CustomersMutationSave() {
        super(UpdateCustomerInfo.ADDRESS, EntityId.class);
    }

    CompletableFuture<SomeEntity> save(CustomersMutation gcontext, EntityId id, CustomerEntry entry) {
        if (id.getProjectId() != gcontext.getProjectId()) {
            throw new GraphQLException("Invalid project id");
        }

        var cmd = UpdateCustomerInfo.builder()
            .id(id)
            .customerName(entry.getCustomerName())
            .customerCityName(entry.getCustomerCityName())
            .customerAddress(entry.getCustomerAddress())
            .build();
        return super.ask(cmd).thenApply(m -> new SomeEntity(m.getProjectId(), m.getId(), m.getVersion()));
    }
}

