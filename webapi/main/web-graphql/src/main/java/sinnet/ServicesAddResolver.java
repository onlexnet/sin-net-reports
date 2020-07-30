package sinnet;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;

/** FixMe. */
@Component
public class ServicesAddResolver implements GraphQLResolver<Services> {

    /**
     * FixMe.
     * @param ignored ignored
     * @param entry fixme
     * @return fixme
     */
    public ServiceModel add(final Services ignored,
                            final ServiceEntry entry) {
        return new ServiceModel(
            entry.getForWhatCustomer(), entry.getWhenProvided());
    }
}
