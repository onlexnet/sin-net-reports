package sinnet;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import sinnet.gql.CustomersMutation;

@GraphQLApi
@ApplicationScoped
public class CustomersMutationRemove {

  public Boolean remove(@Source CustomersMutation self, @NonNull Entity id) {
    //     if (!Objects.equals(id.getProjectId(), gcontext.getProjectId())) {
    //   throw new GraphQLException("Invalid project id");
    // }

    return false;
  }
}
