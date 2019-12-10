package net.siudek;

import java.util.Collections;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import org.springframework.stereotype.Component;

@Component
public class Query implements GraphQLQueryResolver {

    public Iterable<ServiceModel> getServices(ServicesFilter filter) {
        return Collections.emptyList();
    }

}