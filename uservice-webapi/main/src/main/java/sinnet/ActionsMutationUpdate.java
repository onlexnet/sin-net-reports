package sinnet;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

@GraphQLApi
@ApplicationScoped
public class ActionsMutationUpdate {
  public boolean update(@Source ActionsMutation self,
                        @NonNull @Id UUID entityId,
                        int entityVersion,
                        ServiceEntry contant) {
    return false;
  }
}
