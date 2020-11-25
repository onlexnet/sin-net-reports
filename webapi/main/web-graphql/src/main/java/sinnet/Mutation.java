package sinnet;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.Getter;
import sinnet.customers.CustomersOperations;

/** Entry point of all top-leve resolvers. */
@Component
public class Mutation implements GraphQLMutationResolver {

    @Getter
    private ServicesOperations services = new ServicesOperations();

    @Getter
    private CustomersOperations customers = new CustomersOperations();

}
