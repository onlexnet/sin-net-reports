package sinnet;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Source;

@GraphQLApi
@ApplicationScoped
public class CustomersQueryGet {

  public CustomerEntity get(@Source CustomersQuery self, @Id UUID entityId) {
    return null;
  }

}
