package net.siudek;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;

import org.springframework.stereotype.Component;

@Component
public class Mutation implements GraphQLMutationResolver {

    public ServiceModel addService(ServiceEntry entry) {
        return new ServiceModel(entry.getForWhatCustomer(), entry.getWhenProvided());
    }
}
