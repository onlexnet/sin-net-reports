package sinnet.customers;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.Value;
import sinnet.Entity;

@Value
public class CustomerEntity {
    private Entity id;
}

@Component
class CustomerModelResolverPayload
    implements GraphQLResolver<CustomerEntity> {
    CustomerEntry getData(CustomerEntity gcontext) {
        return null;
    }
}

