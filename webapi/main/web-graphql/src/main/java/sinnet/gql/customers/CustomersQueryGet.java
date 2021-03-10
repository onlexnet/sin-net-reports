package sinnet.gql.customers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import sinnet.gql.AskTemplate;
import sinnet.gql.MyEntity;
import sinnet.gql.SomeEntity;
import sinnet.bus.query.FindCustomer;

@Component
public class CustomersQueryGet extends AskTemplate<FindCustomer.Ask, FindCustomer.Reply>
                                implements GraphQLResolver<CustomersQuery> {

  public CustomersQueryGet() {
    super(FindCustomer.Ask.ADDRESS, FindCustomer.Reply.class);
  }

  CompletableFuture<Optional<CustomerEntity>> get(CustomersQuery gcontext, MyEntity entityId) {
    var query = new FindCustomer.Ask(gcontext.getProjectId(), entityId.getEntityId());
    return super
        .ask(query)
        .thenApply(it -> {
          var gqlId = new SomeEntity(gcontext.getProjectId(), it.getEntityId(), it.getEntityVersion());
          var gqlValue = it.getValue();
          var gqlSecrets = it.getSecrets();
          var gqlSecretsEx = it.getSecretsEx();
          var gqlContacts = it.getContacts();
          var result = new CustomerEntity(gqlId, gqlValue, gqlSecrets, gqlSecretsEx, gqlContacts);
          return Optional.of(result);
        });
  }
}

