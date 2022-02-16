package sinnet;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

@GraphQLApi
@ApplicationScoped
public class CustomersMutationSave {

  public Entity save(@Source CustomersMutation self,
                         @NonNull Entity id,
                         @NonNull CustomerInput entry,
                         @NonNull CustomerSecretInput[] secrets,
                         @NonNull CustomerSecretExInput[] secretsEx,
                         @NonNull CustomerContactInput[] contacts) {
    
    // var maybeRequestor = identityProvider.getCurrent();
    // if (!maybeRequestor.isPresent()) {
    //   throw new GraphQLException("Access denied");
    // }

    // if (!Objects.equals(id.getProjectId(), gcontext.getProjectId())) {
    //   throw new GraphQLException("Invalid project id");
    // }

    return null;
  }
    
}
