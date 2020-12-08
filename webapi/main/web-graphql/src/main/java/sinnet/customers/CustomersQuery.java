package sinnet.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.Value;

@Value
class CustomersQuery {
    private UUID projectId;
}

@Component
class CustomerQueryResolver implements GraphQLQueryResolver {

    public CustomersQuery getCustomers(UUID projectId) {
        return new CustomersQuery(projectId);
    }

}
