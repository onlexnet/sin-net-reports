package sinnet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.Getter;

/** fixme. */
@Component
public class Mutation implements GraphQLMutationResolver {

    @Autowired
    @Getter
    private ServicesOperations services;

}
