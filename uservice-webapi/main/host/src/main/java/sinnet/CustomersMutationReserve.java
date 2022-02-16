package sinnet;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

@GraphQLApi
@ApplicationScoped
public class CustomersMutationReserve {

  public @NonNull Entity reserve(@Source CustomersMutation self) {
    return null;
  }
}
