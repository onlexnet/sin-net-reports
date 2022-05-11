package sinnet.gql.api;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import sinnet.gql.models.CommonMapper;
import sinnet.gql.models.ProjectsQuery;

@GraphQLApi
@Slf4j
public class ProjectsQueryAdd implements CommonMapper {

  public @NonNull Uni<sinnet.gql.models.Entity> save(@Source ProjectsQuery self, @NonNull String name) {
    return Uni.createFrom().nullItem();
  }
    
}
