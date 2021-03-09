package sinnet.gql.customers;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import sinnet.gql.SomeEntity;
import sinnet.models.EntityId;

@Component
public class CustomersMutationReserve implements GraphQLResolver<CustomersMutation> {

    SomeEntity reserve(CustomersMutation gcontext) {
        var entity = EntityId.anyNew(gcontext.getProjectId());
        return new SomeEntity(entity.getProjectId(), entity.getId(), entity.getVersion());
    }
}

