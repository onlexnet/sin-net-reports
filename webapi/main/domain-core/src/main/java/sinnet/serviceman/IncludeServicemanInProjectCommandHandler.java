package sinnet.serviceman;

import io.vertx.core.Future;
import sinnet.CommandHandlerBase;
import sinnet.bus.commands.IncludeServicemanInProject;
import sinnet.bus.commands.IncludeServicemanInProject.Command;
import sinnet.models.EntityId;

public class IncludeServicemanInProjectCommandHandler
       extends CommandHandlerBase<IncludeServicemanInProject.Command, EntityId> {

  protected IncludeServicemanInProjectCommandHandler(Class<Command> requestClass) {
    super(IncludeServicemanInProject.Command.class);
  }

  @Override
  protected Future<EntityId> onRequest(Command request) {
    return Future.failedFuture("Not yet implemented");
  }
  
}
