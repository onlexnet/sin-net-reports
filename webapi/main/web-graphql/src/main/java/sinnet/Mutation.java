package sinnet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.Getter;

/** Entry point of all top-leve resolvers. */
@Component
public class Mutation implements GraphQLMutationResolver {

    @Autowired
    @Getter
    private ServicesOperations services;

    @Autowired
    @Getter
    private CustomersOperations customers;

}
