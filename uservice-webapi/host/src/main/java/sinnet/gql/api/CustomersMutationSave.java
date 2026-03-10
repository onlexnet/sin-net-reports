package sinnet.gql.api;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.flow.request.CustomerUpdateCommand;
import sinnet.app.lib.TimeProvider;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.domain.models.CustomerValue;
import sinnet.gql.models.CustomerContactInputGql;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretInput;
import sinnet.gql.models.EntityGql;
import sinnet.gql.models.SomeEntityGql;

/** Fixme. */
@Controller
@RequiredArgsConstructor
class CustomersMutationSave {

  private final TimeProvider timeProvider;

  private final CustomersPortIn service;
  private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

  @SchemaMapping
  public SomeEntityGql save(CustomersMutation self,
                            @Argument EntityGql id,
                            @Argument CustomerInput entry,
                            @Argument List<CustomerSecretInput> secrets,
                            @Argument List<CustomerSecretExInput> secretsEx,
                            @Argument List<CustomerContactInputGql> contacts) {

    var changedWhen = timeProvider.now();
    var changedWho = self.userToken().requestorEmail();

    var customerVal = new CustomerValue(customerMapper.toDomain(entry), secrets, secretsEx, contacts);
    var request = new CustomerUpdateCommand(
      // user context
      self.userToken(),
      // customer data
      id,
      customerVal,
      // change metadata
      changedWhen,
      changedWho);
    var result = service.update(request);
    return customerMapper.toGql(result.getEntityId());
  }

}
