package sinnet.domain.serviceman;

import org.jmolecules.architecture.cqrs.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import sinnet.bus.commands.IncludeServicemanInProject;
import sinnet.models.EntityId;
import sinnet.vertx.CommandHandlerBase;
import sinnet.vertx.TopLevelVerticle;

@Component
public class ServicemanCommandHandlers extends AbstractVerticle implements TopLevelVerticle {

  @Autowired
  private PgPool pgPool;
  
  @Autowired
  private ServicemanRepository repository;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.eventBus().consumer(IncludeServicemanInProject.Command.ADDRESS, new IncludeServicemanInProjectHandler());
    super.start(startPromise);
  }

  final class IncludeServicemanInProjectHandler
        extends CommandHandlerBase<IncludeServicemanInProject.Command, EntityId> {

    IncludeServicemanInProjectHandler() {
      super(IncludeServicemanInProject.Command.class);
    }

    @Override
    @CommandHandler
    protected Future<EntityId> onRequest(IncludeServicemanInProject.Command msg) {
      var entityId = EntityId.anyNew(msg.getProjectId());
      var state = new ServicemanState(entityId, msg.getEmail(), msg.getFullName());
      return repository.write(pgPool, state);
    }
  }
}
