package sinnet.domain.project;

import java.util.UUID;

import io.vavr.collection.Array;
import lombok.Value;
import sinnet.models.ProjectId;

public interface Project {

  interface Repository {

    ProjectId save(UUID projectId, int version);

    Array<Entity> get(String filterByEmail);
  }

  @Value
  class Entity {
    private ProjectId id;
    private String name;
  }
}
