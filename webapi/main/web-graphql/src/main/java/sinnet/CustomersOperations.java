package sinnet;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import io.vavr.collection.List;
import lombok.Data;
import lombok.Value;

@Component
public class CustomersOperations {
}

@Component
class CustomersOperationsResolverList
       implements GraphQLResolver<CustomersOperations> {

    List<CustomerModel> list(CustomersOperations gcontext) {
        return List.empty();
    }
}

@Component
class CustomersOperationsResolverGet
      implements GraphQLResolver<CustomersOperations> {

    Optional<CustomerModel> get(CustomersOperations gcontext, UUID entityId) {
        return Optional.empty();
    }
}

@Component
class CustomersOperationsResolverAddNew
      implements GraphQLResolver<CustomersOperations> {

    MyEntity addNew(CustomersOperations gcontext, CustomerEntry entry) {
        return null;
    }
}

@Data
class CustomerEntry {
    private String customerName;
    private String customerCityName;
    private String customerAddress;
}

@Value
class CustomerModel {
    private String customerName;
    private String customerCityName;
    private String customerAddress;
}

@Value
class CustomerEntity {
    private Entity id;
}

@Component
class CustomerModelResolverPayload
      implements GraphQLResolver<CustomerEntity> {
    CustomerEntry getData(CustomerEntity gcontext) {
        return null;
    }
}
