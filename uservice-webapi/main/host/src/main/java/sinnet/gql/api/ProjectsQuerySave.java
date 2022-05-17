package sinnet.gql.api;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import sinnet.gql.models.CommonMapper;
import sinnet.gql.models.Entity;
import sinnet.gql.models.ProjectsQuery;

@GraphQLApi
@Slf4j
public class ProjectsQuerySave implements CommonMapper {

  public @NonNull Uni<Entity> save(@Source ProjectsQuery self, @NonNull String name) {
    var result = new Entity();
    result.setEntityId("some id");
    result.setProjectId("some project id");
    result.setEntityVersion(42);
    return Uni.createFrom().item(result);
  }
    
}