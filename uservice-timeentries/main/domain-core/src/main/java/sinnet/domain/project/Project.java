package sinnet.domain.project;

import java.util.UUID;

import io.vavr.collection.Array;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;
import lombok.Value;
import sinnet.models.ProjectId;

public interface Project {

  interface Repository {

    Future<ProjectId> save(SqlClient sqlClient, UUID projectId, int version);

    Future<Array<Entity>> get(SqlClient sqlClient, String filterByEmail);
  }

  @Value
  class Entity {
    private ProjectId id;
    private String name;
  }
}
