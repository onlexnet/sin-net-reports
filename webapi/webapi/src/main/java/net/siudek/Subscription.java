package net.siudek;

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

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
