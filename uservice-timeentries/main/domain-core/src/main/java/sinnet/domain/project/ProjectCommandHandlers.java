package sinnet.domain.project;

import java.util.UUID;

import org.jmolecules.architecture.cqrs.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.jdbcclient.JDBCPool;
import sinnet.bus.commands.CreateNewProject;
import sinnet.models.ProjectId;
import sinnet.vertx.CommandHandlerBase;
import sinnet.vertx.TopLevelVerticle;

@Component
public class ProjectCommandHandlers extends AbstractVerticle 
                                    implements TopLevelVerticle {

  private final Project.Repository repository;
  private final JDBCPool dbPool;

  @Autowired
  public ProjectCommandHandlers(JDBCPool dbPool, Project.Repository repository) {
    this.dbPool = dbPool;
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
      return repository.save(dbPool, id, 0);
    }
  }
}
