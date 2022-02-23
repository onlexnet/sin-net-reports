package sinnet;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Source;

@GraphQLApi
@ApplicationScoped
public class CustomersQueryGet {

  public CustomerEntity get(@Source CustomersQuery self, @Id String entityId) {
    return null;
  }

}
