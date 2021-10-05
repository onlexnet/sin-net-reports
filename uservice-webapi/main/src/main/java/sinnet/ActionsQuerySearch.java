package sinnet;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

@GraphQLApi
@ApplicationScoped
public class ActionsQuerySearch {
  public @NonNull ServicesSearchResult search(@Source ActionsQuery self, ServiceFilter filter) {
    return new ServicesSearchResult();
  }
}
