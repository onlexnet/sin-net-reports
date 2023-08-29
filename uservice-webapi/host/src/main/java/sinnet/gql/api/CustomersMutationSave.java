package sinnet.gql.api;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import io.vavr.collection.Array;
import lombok.RequiredArgsConstructor;
import sinnet.gql.models.CustomerContactInputGql;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretInput;
import sinnet.gql.models.EntityGql;
import sinnet.gql.models.SomeEntityGql;
import sinnet.grpc.CustomersGrpcFacade;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.UpdateCommand;

/** Fixme. */
@Controller
@RequiredArgsConstructor
class CustomersMutationSave implements CustomerMapper {

  private final CustomersGrpcFacade service;

  @SchemaMapping
  public SomeEntityGql save(CustomersMutation self,
                            @Argument EntityGql id,
                            @Argument CustomerInput entry,
                            @Argument List<CustomerSecretInput> secrets,
                            @Argument List<CustomerSecretExInput> secretsEx,
                            @Argument List<CustomerContactInputGql> contacts) {

    var request = UpdateCommand.newBuilder()
        .setUserToken(self.getUserToken())
        .setModel(CustomerModel.newBuilder()
            .setId(toGrpc(id))
            .setValue(toGrpc(entry))
            .addAllSecrets(secrets.stream().map(this::toGrpc).toList())
            .addAllSecretEx(secretsEx.stream().map(this::toGrpc).toList())
            .addAllContacts(contacts.stream().map(this::toGrpc).toList())
            .build())
        .build();
    var result = service.update(request);
    return this.toGql(result.getEntityId());
  }

}
