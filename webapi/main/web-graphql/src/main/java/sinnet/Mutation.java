package sinnet;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import sinnet.customers.CustomersOperations;
import sinnet.projects.ProjectToken;

/** Entry point of all top-leve resolvers. */
@Component
public class Mutation implements GraphQLMutationResolver {

    public ServicesOperations getServices(ProjectToken token) {
        return new ServicesOperations(token.getProjectId());
    }

    public CustomersOperations getCustomers(ProjectToken token) {
        return new CustomersOperations(token.getProjectId());
    }

}
