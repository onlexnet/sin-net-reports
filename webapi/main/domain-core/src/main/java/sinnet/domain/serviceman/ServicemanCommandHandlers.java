package sinnet.domain.serviceman;

import org.jmolecules.architecture.cqrs.annotation.CommandHandler;
import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import sinnet.CommandHandlerBase;
import sinnet.TopLevelVerticle;
import sinnet.bus.commands.IncludeServicemanInProject;
import sinnet.models.EntityId;

@Component
public class ServicemanCommandHandlers extends AbstractVerticle implements TopLevelVerticle {

  private final ServicemanRepository repository;

  public ServicemanCommandHandlers(ServicemanRepository repository) {
    this.repository = repository;
  }

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
      return Future.failedFuture("not yet implemented");
    }
  }
}
