package net.onlex;

import java.util.List;

import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.graphql.client.typesafe.api.NestedParameter;
import lombok.Data;
import lombok.Value;

// headers: https://smallrye.io/smallrye-graphql/1.4.2/typesafe-client-headers/

// @GraphQLClientApi(configKey = "beta-sinnetapp") // - in case of testing remove env
@GraphQLClientApi(configKey = "local-sinnetapp") // - in case of testing local stack
public interface AppApi {

  @Mutation("Projects")
  SaveProjectQuery saveProject(@NestedParameter("save") @NonNull String name);

  @Mutation("Projects")
  RemoveProjectQuery removeProject(@NestedParameter("remove") @NonNull ProjectId projectId);

  @Query("Projects")
  ProjectListQuery projectList(@NestedParameter("list") @NonNull String name);

  @Query("Projects")
  ProjectsQuery3 projectsCount();

  @Data
  class ProjectInfo {
    private String id;
    private String name;
  }


  @Data
  class SaveProjectQuery {
    ProjectEntity save;
  }

  @Data
  class RemoveProjectQuery {
    Boolean remove;
  }
  @Data
  class ProjectListQuery {
    private List<ProjectEntity> list;
  }

  @Data
  class ProjectsQuery3 {
    private int numberOfProjects;
  }

  @Data
  class Entity {
    String projectId;
    String entityId;
    int entityVersion;
  }

  @Data
  class ProjectEntity {
    private @NonNull Entity entity;
    private @NonNull String name;
  }

  @Value
  class ProjectId {
    private String id;
    private long tag;
  }

}
