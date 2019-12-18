package net.siudek;

import java.util.Collections;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import org.springframework.stereotype.Component;

/** FixMe. */
@Component
public class Query implements GraphQLQueryResolver {

    /**
     * FixMe.
     *
     * @param filter fixme.
     * @return fixme
     */
    public Iterable<ServiceModel> getServices(final ServicesFilter filter) {
        return Collections.emptyList();
    }

}
