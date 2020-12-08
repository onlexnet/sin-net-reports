package sinnet.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.Value;

@Value
class CustomersMutation {
    private UUID projectId;
}

@Component
class CustomerMutationResolver implements GraphQLMutationResolver {

    public CustomersMutation getCustomers(UUID projectId) {
        return new CustomersMutation(projectId);
    }

}
