package sinnet;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;

import org.springframework.stereotype.Component;

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
