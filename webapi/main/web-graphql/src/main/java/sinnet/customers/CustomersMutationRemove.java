package sinnet.customers;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.GraphQLException;
import graphql.kickstart.tools.GraphQLResolver;
import sinnet.AskTemplate;
import sinnet.IdentityProvider;
import sinnet.MyEntity;
import sinnet.bus.commands.RemoveCustomer;
import sinnet.models.EntityId;

@Component
public class CustomersMutationRemove extends AskTemplate<RemoveCustomer.Command, RemoveCustomer.Result>
                                     implements GraphQLResolver<CustomersMutation>,
                                     RemoveCustomer {
    @Autowired
    private IdentityProvider identityProvider;

    public CustomersMutationRemove() {
        super(Command.ADDRESS, Result.class);
    }

    CompletableFuture<Boolean> remove(CustomersMutation gcontext, MyEntity id) {
        var maybeRequestor = identityProvider.getCurrent();
        if (!maybeRequestor.isPresent()) throw new GraphQLException("Access denied");

        if (!Objects.equals(id.getProjectId(), gcontext.getProjectId())) {
            throw new GraphQLException("Invalid project id");
        }

        var entityId = EntityId.of(id.getProjectId(), id.getEntityId(), id.getEntityVersion());
        var cmd = Command.builder().id(entityId).build();
        return super.ask(cmd).thenApply(it -> it.getValue());
    }
}

