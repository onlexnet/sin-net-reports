package sinnet.customers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.Value;
import sinnet.IdentityProvider;

@Value
class CustomersMutation {
    private UUID projectId;
}

@Component
class CustomerMutationResolver implements GraphQLMutationResolver {

    @Autowired
    private IdentityProvider identityProvider;

    public CustomersMutation getCustomers(UUID projectId) {
        return new CustomersMutation(projectId);
    }

}
