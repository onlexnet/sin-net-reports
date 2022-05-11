package net.onlex;

import java.util.List;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.graphql.client.typesafe.api.Header;
import lombok.Data;

// headers: https://smallrye.io/smallrye-graphql/1.4.2/typesafe-client-headers/

// @GraphQLClientApi(configKey = "beta-sinnetapp")
@GraphQLClientApi(configKey = "local-sinnetapp")
public interface AppApi {

  List<ProjectInfo> availableProjects(@Header(name = "Authorization") String authorizationHeaderValue);

  ProjectsQuery projects(@Header(name = "Authorization") String authorizationHeaderValue);

  @Data
  class ProjectInfo {
    private String id;
    private String name;
  }

  interface ProjectsQuery {
    Entity save(String name);
  }

  @Data
  class Entity {
    String projectId;
    String entityId;
    int entityVersion;
  }

}
