package sinnet;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

@GraphQLApi
@ApplicationScoped
public class CustomersQueryList {

  public @NonNull CustomerEntity[] list(@Source CustomersQuery self) {
    return ArrayUtils.toArray();
  }

}
