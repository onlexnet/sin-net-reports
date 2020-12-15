package sinnet.customers;

import java.util.concurrent.CompletableFuture;

import com.google.common.base.Objects;

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

    CompletableFuture<SomeEntity> save(CustomersMutation gcontext, SomeEntity id, CustomerEntry entry) {
        if (!Objects.equal(id.getProjectId(), gcontext.getProjectId())) {
            throw new GraphQLException("Invalid project id");
        }

        var eid = new EntityId(id.getProjectId(), id.getEntityId(), id.getEntityVersion());
        var cmd = UpdateCustomerInfo.builder()
            .id(eid)
            .customerName(entry.getCustomerName())
            .customerCityName(entry.getCustomerCityName())
            .customerAddress(entry.getCustomerAddress())
            .build();
        return super.ask(cmd).thenApply(m -> new SomeEntity(m.getProjectId(), m.getId(), m.getVersion()));
    }
}

