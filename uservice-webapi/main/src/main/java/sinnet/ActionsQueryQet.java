package sinnet;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Source;

@GraphQLApi
@ApplicationScoped
public class ActionsQueryQet {
  public ServiceModel get(@Source ActionsQuery self, @Id UUID actionId) {
    return null;
  }
}
