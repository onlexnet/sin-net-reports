package net.onlex;

import java.util.List;

import org.eclipse.microprofile.graphql.NonNull;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.graphql.client.typesafe.api.NestedParameter;
import lombok.Data;

// headers: https://smallrye.io/smallrye-graphql/1.4.2/typesafe-client-headers/

// @GraphQLClientApi(configKey = "beta-sinnetapp") // - in case of testing remove env
@GraphQLClientApi(configKey = "local-sinnetapp") // - in case of testing local stack
public interface AppApi {

  List<ProjectInfo> availableProjects();

  ProjectsQuery Projects(@NestedParameter("save") @NonNull String name);

  @Data
  class ProjectInfo {
    private String id;
    private String name;
  }


  @Data
  class ProjectsQuery {
    Entity save;
  }

  @Data
  class Entity {
    String projectId;
    String entityId;
    int entityVersion;
  }

}
