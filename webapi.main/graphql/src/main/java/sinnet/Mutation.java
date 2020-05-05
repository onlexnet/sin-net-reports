package sinnet;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;

/** FixMe. */
@Component
public class Mutation implements GraphQLMutationResolver {

    /**
     * FixMe.
     * @param entry fixme
     * @return fixme
     */
    public ServiceModel addService(final ServiceEntry entry) {
        return new ServiceModel(
            entry.getForWhatCustomer(), entry.getWhenProvided());
    }
}
