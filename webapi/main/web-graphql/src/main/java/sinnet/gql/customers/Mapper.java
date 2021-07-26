package sinnet.gql.customers;

import java.util.Optional;

import io.vavr.control.Option;
import sinnet.gql.SomeEntity;
import sinnet.read.CustomerProjection;

public class Mapper {

  public static Optional<CustomerEntity> gql(Option<CustomerProjection.CustomerModel> model) {
    return model
      .map(it -> {
        var gqlId = SomeEntity.of(it.getId());
        var gqlValue = it.getValue();
        var gqlSecrets = it.getSecrets();
        var gqlSecretsEx = it.getSecretsEx();
        var gqlContacts = it.getContacts();
        return new CustomerEntity(gqlId, gqlValue, gqlSecrets, gqlSecretsEx, gqlContacts);
      })
      .toJavaOptional();
  }
}
