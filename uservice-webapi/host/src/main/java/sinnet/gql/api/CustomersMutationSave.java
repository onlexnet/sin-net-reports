package sinnet.gql.api;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.gql.models.CustomerContactInputGql;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretInput;
import sinnet.gql.models.EntityGql;
import sinnet.gql.models.SomeEntityGql;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.UpdateCommand;
import sinnet.infra.TimeProvider;
import sinnet.infra.adapters.grpc.CustomersGrpcFacade;

/** Fixme. */
@Controller
@RequiredArgsConstructor
class CustomersMutationSave {

  private final TimeProvider timeProvider;

  private final CustomersGrpcFacade service;
  private final CustomerMapper customerMapper;

  @SchemaMapping
  public SomeEntityGql save(CustomersMutation self,
                            @Argument EntityGql id,
                            @Argument CustomerInput entry,
                            @Argument List<CustomerSecretInput> secrets,
                            @Argument List<CustomerSecretExInput> secretsEx,
                            @Argument List<CustomerContactInputGql> contacts) {

    var changedWhen = timeProvider.now();
    var changedWho = self.userToken().getRequestorEmail();
    
    var request = UpdateCommand.newBuilder()
        .setUserToken(self.userToken())
        .setModel(CustomerModel.newBuilder()
            .setId(customerMapper.toGrpc(id))
            .setValue(customerMapper.toGrpc(entry))
            .addAllSecrets(secrets.stream().map(it -> customerMapper.toGrpc(it, changedWhen, changedWho)).toList())
            .addAllSecretEx(secretsEx.stream().map(it -> customerMapper.toGrpc(it, changedWhen, changedWho)).toList())
            .addAllContacts(contacts.stream().map(customerMapper::toGrpc).toList())
            .build())
        .build();
    var result = service.update(request);
    return customerMapper.toGql(result.getEntityId());
  }

}
