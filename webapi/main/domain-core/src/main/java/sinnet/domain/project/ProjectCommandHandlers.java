package sinnet.domain.project;

import java.time.LocalDateTime;
import java.util.UUID;

import org.jmolecules.architecture.cqrs.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.collection.List;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import sinnet.CommandHandlerBase;
import sinnet.TopLevelVerticle;
import sinnet.bus.commands.CreateNewProject;
import sinnet.models.EntityId;
import sinnet.models.ProjectId;

@Component
public class ProjectCommandHandlers extends AbstractVerticle 
                                    implements TopLevelVerticle {

  private final Project.Repository repository;
  private final PgPool pgPool;

  @Autowired
  public ProjectCommandHandlers(PgPool pgPool, Project.Repository repository) {
    this.pgPool = pgPool;
    this.repository = repository;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.eventBus().consumer(CreateNewProject.Command.ADDRESS, new CreateNewProjectHandler());
    super.start(startPromise);
  }

  final class CreateNewProjectHandler
        extends CommandHandlerBase<CreateNewProject.Command, ProjectId>
        implements CreateNewProject {

    CreateNewProjectHandler() {
      super(Command.class);
    }

    @Override
    @CommandHandler
    protected Future<ProjectId> onRequest(Command msg) {
      var id = UUID.randomUUID();
      return repository.save(pgPool, id, 0);
    }
  }
}
