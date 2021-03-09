package sinnet.gql;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLSubscriptionResolver;

/** FixMe. */
@Component
public class Subscription implements GraphQLSubscriptionResolver {

    /**
     * FixMe.
     *
     * @return fixme
     */
    public Publisher<Integer> time() {
        return null;
    }

}
