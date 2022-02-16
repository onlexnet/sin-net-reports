package sinnet;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.smallrye.mutiny.Uni;

@GraphQLApi
@ApplicationScoped
public class ActionsQuerySearch {

  public @NonNull Uni<ServicesSearchResult> search(@Source ActionsQuery self, ServiceFilter filter) {
    return Uni.createFrom().nullItem();
  }
}
