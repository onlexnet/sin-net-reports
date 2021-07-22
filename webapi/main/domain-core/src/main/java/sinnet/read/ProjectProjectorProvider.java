package sinnet.read;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.collection.Array;
import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import sinnet.domain.project.Project;
import sinnet.models.Email;

/**
 * Default implementation of {@see ProjectProjector.Provider}.
 */
@Component
public class ProjectProjectorProvider implements ProjectProjector, ProjectProjector.Provider {

  @Autowired
  private Project.Repository projectRepository;

  @Autowired
  private PgPool pgPool;

  @Override
  public Future<Array<FindByServicemanModel>> findByServiceman(Email email) {
    return projectRepository
      .get(pgPool, email.getValue())
      .map(it -> it.map(o -> new FindByServicemanModel(o.getId(), o.getName())));
  }
  
}
