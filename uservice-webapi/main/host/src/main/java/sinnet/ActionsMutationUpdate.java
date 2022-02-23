package sinnet;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

@GraphQLApi
@ApplicationScoped
public class ActionsMutationUpdate {
  public boolean update(@Source ActionsMutation self,
                        @NonNull @Id String entityId,
                        int entityVersion,
                        ServiceEntry content) {
    return false;
  }
}
